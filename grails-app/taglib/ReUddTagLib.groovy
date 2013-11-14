/*
 * Copyright (c) 2009-2013 Björn Granvik & Jonas Andersson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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