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

import org.reudd.node.DataNodeFactory;


class ReUddTagLib {
	
	static namespace = "reudd"
	
	def attributeInput = { attrs ->
		def key = attrs.key
		def value = attrs.value
		def edit = attrs.edit.toBoolean()
		
		def result = "$value"
		if (edit) {
			result = """<input type="text" value="$value" name="attribute-$key" class="value"/>"""
		}
		out << result
	}
	
	def hBox = { attrs, body ->
		out << """<div class="hBox">""" + body() + "</div>"
	}
	
	def vBox = { attrs, body ->
		out << """<div class="vBox">""" + body() + "</div>"
	}
	
	def box = { attrs, body ->
		def width = ""
		if (attrs.width) {
			width = "width:${attrs.width};"
		}
		def height = ""
		if (attrs.height) {
			height = "height:${attrs.height};"
		}
		out << """<div style="$width$height">""" + body() + "</div>"
	}
	
	def hBoxAuto = { attrs ->
		def node = attrs.data[0]
		def attributes = attrs.data[1..-1]
		def edit = false
		if (params.action == "editNode" || params.action == "addNode") {
			edit = true
		}
		out << """<div class="hBox">"""
		attributes.each {
			def key = it
			def value = node.attributes[key]
			
			out << """<div class="boxAttribute">"""
			out << """<div>$it<img alt=":" src="/ReUDD/images/colon-small.png"/></div>"""
			out << """<div>"""
			if (edit) {
				out << """<input type="text" value="$value" name="attribute-$key" class="value"/>"""
			} else {
				out << """$value"""
			}
			out << """</div></div>"""
		}
		out << "</div>"
	}
	
	def vBoxAuto = { attrs, body ->
		def node = attrs.data[0]
		def attributes = attrs.data[1..-1]
		def edit = false
		if (params.action == "editNode" || params.action == "addNode") {
			edit = true
		}
		out << """<div class="vBox">"""
		attributes.each {
			def key = it
			def value = node.attributes[key]
			
			out << """<div class="boxAttribute">"""
			out << """<div>$it<img alt=":" src="/ReUDD/images/colon-small.png"/></div>"""
			out << """<div>"""
			if (edit) {
				out << """<input type="text" value="$value" name="attribute-$key" class="value"/>"""
			} else {
				out << """$value"""
			}
			out << """</div></div>"""
		}
		out << "</div>"
	}
	
	def img = { attrs ->
		def name= attrs.name ?: ""
		def alt = attrs.alt ?: "img"
		def onClick = attrs.onClick ?: ""
		def clazz = attrs["class"] ?: ""
		def ext = ".png"
		if (attrs.ext) {
			ext = ".${attrs.ext}"
		}
		out << """<img class="$clazz" onClick="$onClick" alt="$alt" src="/ReUDD/images/$name$ext"/>"""
	}
	
}