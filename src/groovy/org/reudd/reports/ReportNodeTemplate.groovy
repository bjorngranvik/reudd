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