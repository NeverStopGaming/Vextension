package eu.vironlab.vextension.util

import eu.vironlab.vextension.item.ItemStackLike

object Vextension {
    var itemKeyDone: ((ItemStackLike, String) -> Unit)? = null
    var itemVerifyUniqueKey: ((String, ItemStackLike) -> Boolean)? = null
}