/**
 *   Copyright © 2020 | vironlab.eu | All Rights Reserved.<p>
 * <p>
 *      ___    _______                        ______         ______  <p>
 *      __ |  / /___(_)______________ _______ ___  / ______ ____  /_ <p>
 *      __ | / / __  / __  ___/_  __ \__  __ \__  /  _  __ `/__  __ \<p>
 *      __ |/ /  _  /  _  /    / /_/ /_  / / /_  /___/ /_/ / _  /_/ /<p>
 *      _____/   /_/   /_/     \____/ /_/ /_/ /_____/\__,_/  /_.___/ <p>
 *<p>
 *    ____  _______     _______ _     ___  ____  __  __ _____ _   _ _____ <p>
 *   |  _ \| ____\ \   / / ____| |   / _ \|  _ \|  \/  | ____| \ | |_   _|<p>
 *   | | | |  _|  \ \ / /|  _| | |  | | | | |_) | |\/| |  _| |  \| | | |  <p>
 *   | |_| | |___  \ V / | |___| |__| |_| |  __/| |  | | |___| |\  | | |  <p>
 *   |____/|_____|  \_/  |_____|_____\___/|_|   |_|  |_|_____|_| \_| |_|  <p>
 *<p>
 *<p>
 *   This program is free software: you can redistribute it and/or modify<p>
 *   it under the terms of the GNU General Public License as published by<p>
 *   the Free Software Foundation, either version 3 of the License, or<p>
 *   (at your option) any later version.<p>
 *<p>
 *   This program is distributed in the hope that it will be useful,<p>
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of<p>
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the<p>
 *   GNU General Public License for more details.<p>
 *<p>
 *   You should have received a copy of the GNU General Public License<p>
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.<p>
 *<p>
 *   Contact:<p>
 *<p>
 *     Discordserver:   https://discord.gg/wvcX92VyEH<p>
 *     Website:         https://vironlab.eu/ <p>
 *     Mail:            contact@vironlab.eu<p>
 *<p>
 */

package eu.vironlab.vextension.discord

import eu.vironlab.vextension.Vextension
import eu.vironlab.vextension.VextensionAPI
import eu.vironlab.vextension.discord.command.CommandManager
import eu.vironlab.vextension.discord.command.executor.CommandExecutor
import eu.vironlab.vextension.document.ConfigDocument
import eu.vironlab.vextension.document.initDocumentManagement
import java.io.File
import net.dv8tion.jda.api.JDA


abstract class DiscordBot(loadJda: Boolean = true) : Vextension {

    abstract var jda: JDA
    abstract var commandManager: CommandManager
    protected val token: String

    //protected val connectionData: RemoteConnectionData
    protected val config: ConfigDocument
    //final override var databaseClient: DatabaseClient

    init {
        initDocumentManagement()
        //OldDependencyLoader.init()
        if (loadJda) {
            DiscordUtil.loadJDA()
        }
        this.config = ConfigDocument(File("config.json"))
        this.config.loadConfig()
        this.token = this.config.getString("token", "Please enter Token here")
        val clientType: String = this.config.getString("databaseType", "mongodb")
        /*this.connectionData = this.config.get(
            "database",
            object : TypeToken<RemoteConnectionData>() {}.type,
            RemoteConnectionData("localhost", 27017, "discord", "discord", "password")
        )
        config.saveConfig()
        when(clientType.toLowerCase()) {
            "mongodb" -> {
                this.databaseClient = MongoDatabaseClient(this.connectionData)
            }
            "mysql" -> {
                this.databaseClient = SqlDatabaseClient(this.connectionData)
            }
            else -> {
                throw UnsupportedOperationException("Please type 'mongodb' or 'mysql' as databaseType")
            }
        }
        this.databaseClient.init()
        DiscordUtil.userDatabase = this.databaseClient.getDatabase("discord_users")
        */
        VextensionAPI.initialize(this)
    }

    fun shutdown() {
        //this.databaseClient.close()
        this.jda.shutdown()
    }

    fun registerCommand(executor: CommandExecutor) {
        this.commandManager.register(executor)
    }

    fun registerListener(listener: Any) {
        this.jda.addEventListener(listener)
    }

}