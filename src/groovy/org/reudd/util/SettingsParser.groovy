/*
 * Copyright (c) 2009-2012 Björn Granvik & Jonas Andersson, http://reudd.org
 *
 * This file is part of ReUDD.
 *
 * ReUDD is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Author: Jonas Andersson, jonas@splanaden.se
 */

package org.reudd.util;

class SettingsParser {
	
	static def getSetting(settingName, settingsString) {
		def result = []
		def regExp = /${settingName}:\[(.*?)\](,|$)/
		def matcher = (settingsString =~ regExp)
		if (matcher.matches()) {
			for (string in matcher[0][1].split(',')) {
				result.add string.trim()
			}
		}
		result
	}
	
	static def getAttributeSetting(settingName, settingsString){
		def regExp = /${settingName}:(.*?)(,|$)/
		def matcher = (settingsString =~ regExp)
		if (matcher.matches()) {
			return matcher[0][1]
		}
	}
	
}