package eu.vironlab.vextension.item.builder

import com.google.gson.JsonParser
import eu.vironlab.vextension.bukkit.VextensionBukkit
import eu.vironlab.vextension.extension.random
import eu.vironlab.vextension.factory.Factory
import eu.vironlab.vextension.item.InteractType
import eu.vironlab.vextension.item.ItemStack
import eu.vironlab.vextension.item.ItemStackLike
import eu.vironlab.vextension.item.Material
import eu.vironlab.vextension.sponge.VextensionSponge
import eu.vironlab.vextension.util.ServerType
import eu.vironlab.vextension.util.ServerUtil
import eu.vironlab.vextension.util.Vextension
import net.kyori.adventure.text.Component
import java.util.*

class DynamicItemFactory(
    private var material: (UUID?) -> Material
) : Factory<ItemStack>, ItemStackLike {
    private var name: ((UUID?) -> Component?) = { null }
    fun name(component: Component?) = name { component }
    fun name(name: (UUID?) -> Component?): DynamicItemFactory {
        this.name = name
        return this
    }
    private var amount: ((UUID?) -> Int) = { 1 }
    fun amount(amount: Int) = amount { amount }
    fun amount(amount: (UUID?) -> Int): DynamicItemFactory {
        this.amount = amount
        return this
    }
    private var damage: ((UUID?) -> Int) = { 0 }
    fun damage(damage: Int) = damage { damage }
    fun damage(damage: (UUID?) -> Int): DynamicItemFactory {
        this.damage = damage
        return this
    }
    private var lore: ((UUID?) -> MutableList<Component>) = { mutableListOf() }
    fun lore(lore: MutableList<Component>) = lore { lore }
    fun lore(lore: (UUID?) -> MutableList<Component>): DynamicItemFactory {
        this.lore = lore
        return this
    }
    private var unbreakable: ((UUID?) -> Boolean) = { false }
    fun unbreakable(unbreakable: Boolean) = unbreakable { unbreakable }
    fun unbreakable(unbreakable: (UUID?) -> Boolean): DynamicItemFactory {
        this.unbreakable = unbreakable
        return this
    }
    private var blockInteract: ((UUID?) -> Boolean) = { false }
    fun blockInteract(blockInteract: Boolean) = blockInteract { blockInteract }
    fun blockInteract(blockInteract: (UUID?) -> Boolean): DynamicItemFactory {
        this.blockInteract = blockInteract
        return this
    }
    private var blockClick: ((UUID?) -> Boolean) = { false }
    fun blockClick(blockClick: Boolean) = blockClick { blockClick }
    fun blockClick(blockClick: (UUID?) -> Boolean): DynamicItemFactory {
        this.blockClick = blockClick
        return this
    }
    private var blockDrop: ((UUID?) -> Boolean) = { false }
    fun blockDrop(blockDrop: Boolean) = blockDrop { blockDrop }
    fun blockDrop(blockDrop: (UUID?) -> Boolean): DynamicItemFactory {
        this.blockDrop = blockDrop
        return this
    }

    private var dropHandler: ((ItemStack, UUID) -> Unit)? = null
    fun dropHandler(dropHandler: ((ItemStack, UUID) -> Unit)?): DynamicItemFactory {
        this.dropHandler = dropHandler
        return this
    }

    private var interactHandler: ((ItemStack, UUID, Optional<InteractType>) -> Unit)? = null
    fun interactHandler(interactHandler: ((ItemStack, UUID, Optional<InteractType>) -> Unit)?): DynamicItemFactory {
        this.interactHandler = interactHandler
        return this
    }
    private var clickHandler: ((ItemStack, UUID) -> Unit)? = null
    fun clickHandler(clickHandler: ((ItemStack, UUID) -> Unit)?): DynamicItemFactory {
        this.clickHandler = clickHandler
        return this
    }
    private var permission: ((UUID?) -> String?) = { null }
    fun permission(permission: String?) = permission { permission }
    fun permission(permission: (UUID?) -> String?): DynamicItemFactory {
        this.permission = permission
        return this
    }
    private var skullOwner: ((UUID?) -> UUID?) = { null }
    fun skullOwner(skullOwner: UUID?) = skull { skullOwner }
    fun skull(uuid: (UUID?) -> UUID?): DynamicItemFactory {
        this.skullOwner = uuid
        return this
    }
    private var skullTexture: ((UUID?) -> String?) = { null }
    fun texture(skullTexture: String?) = texture { skullTexture }
    fun texture(skullTexture: (UUID?) -> String?): DynamicItemFactory {
        this.skullTexture = skullTexture
        return this
    }

    private var properties: ((UUID?) -> MutableMap<String, String>) = { mutableMapOf() }
    fun properties(properties: MutableMap<String, String>) = properties { properties }
    fun properties(properties: (UUID?) -> MutableMap<String, String>): DynamicItemFactory {
        this.properties = properties
        return this
    }
    fun create(uuid: UUID?): ItemStack {
        @Suppress("DuplicatedCode") var key: String = String.random(64)
        when (ServerUtil.SERVER_TYPE) {
            ServerType.SPONGE -> {
                while (VextensionSponge.instance.items.containsKey(key))
                    key = String.random(64)
            }
            ServerType.BUKKIT -> {
                while (VextensionBukkit.items.containsKey(key))
                    key = String.random(64)
            }
            else -> while (Vextension.itemVerifyUniqueKey!!.invoke(key, this))
                key = String.random(64)
        }
        Vextension.itemKeyDone?.invoke(this, key)
        return ItemStack(
            material.invoke(uuid),
            name.invoke(uuid),
            amount.invoke(uuid),
            damage.invoke(uuid),
            lore.invoke(uuid),
            unbreakable.invoke(uuid),
            blockDrop.invoke(uuid),
            blockInteract.invoke(uuid),
            blockClick.invoke(uuid),
            key,
            dropHandler,
            interactHandler,
            clickHandler,
            permission.invoke(uuid),
            skullOwner.invoke(uuid),
            skullTexture.invoke(uuid)?.let {
                when {
                    "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)?\$".toRegex().matches(it) -> it
                    try {
                        JsonParser().parse(it);
                        true
                    } catch (e: Exception) {
                        false
                    } -> Base64.getEncoder().encode(it.toByteArray()).decodeToString()
                    else -> Base64.getEncoder().encode("{\"textures\": {\"SKIN\": {\"url\": \"$it\"}}}".toByteArray())
                        .decodeToString()
                }
            },
            properties.invoke(uuid)
        )
    }

    override fun create(): ItemStack = create(null)

    fun blockAll(block: Boolean) = blockAll { block }
    fun blockAll(blockAll: (UUID?) -> Boolean): DynamicItemFactory {
        this.blockDrop = blockAll
        this.blockInteract = blockAll
        this.blockClick = blockAll
        return this
    }

    override fun get(uuid: UUID?): ItemStack = create(uuid)
}
fun dynamicItem(material: (UUID?) -> Material, init: DynamicItemFactory.() -> Unit): DynamicItemFactory {
    return DynamicItemFactory(material).apply(init)
}