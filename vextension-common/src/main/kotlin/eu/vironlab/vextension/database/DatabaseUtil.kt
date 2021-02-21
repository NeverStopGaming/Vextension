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

@file:Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package eu.vironlab.vextension.database

import eu.vironlab.vextension.database.info.ObjectInformation
import eu.vironlab.vextension.document.Document
import eu.vironlab.vextension.document.DocumentManagement
import java.util.*
import kotlin.NoSuchElementException
import kotlin.reflect.KClass


object DatabaseUtil {

    /**
     * Get the Information of the DatabaseObject wich is stored in a File wich will be normally created by the Annotation Processor
     */
    @JvmStatic
    fun <T : Any> getInfo(clazz: KClass<T>): Optional<ObjectInformation> {
        try {
            val document: Document =
                DocumentManagement.jsonStorage()
                    .read(clazz.java.canonicalName, clazz.java.classLoader.getResourceAsStream("eu/vironlab/vextension/database/objects/${clazz.java.canonicalName}.json"))
            return Optional.of(ObjectInformation(
                document.getString("keyField").orElseThrow { NoSuchElementException("Cannot find 'keyField' in Object File") },
                document.getString("key").orElseThrow { NoSuchElementException("Cannot find 'key' in Object File") },
                document.getList<String>("ignoredFields", mutableListOf<String>()),
                document.getMap<String, String>("specificNames").orElseThrow { NoSuchElementException("Cannot find 'specificNames' in Object File") },
            ))
        } catch (e: Exception) {
            e.printStackTrace()
            return Optional.empty()
        }
    }


}