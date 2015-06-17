/*
 * Copyright (c) 2009-2015 Björn Granvik & Jonas Andersson
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

package org.reudd.reports;

import org.neo4j.graphdb.Node;

class ReportNodeTemplate {
	
	final Node underlyingNode
	
	final def id
	
	String title
	
	def body
	
	def parameters
	
	ReportNodeTemplate(Node underlyingNode) {
		this.underlyingNode = underlyingNode
		this.id = underlyingNode.id
		this.title = underlyingNode.hasProperty("title") ? underlyingNode.getProperty("title") : ""
		this.body = underlyingNode.hasProperty("body") ? underlyingNode.getProperty("body") : ""
		if (underlyingNode.hasProperty("parameters")) {
			this.parameters = []
			underlyingNode.getProperty("parameters").each {
				parameters += it
			}
		} else {
			this.parameters = []
		}
	}
	
	def save() {
		underlyingNode.setProperty("title", title)
		underlyingNode.setProperty("body", body)
		String[] stringList = new String[parameters.size()]
		for (int i=0 ; i<stringList.length ; i++) {
			stringList[i] = parameters[i]
		}
		underlyingNode.setProperty("parameters", stringList)
	}
	
	def delete() {
		underlyingNode.getRelationships().each { it.delete() }
		underlyingNode.delete()
	}
	
	def String toString() {
		title
	}
	
}