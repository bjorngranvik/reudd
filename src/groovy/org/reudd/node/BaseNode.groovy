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

package org.reudd.node

import org.neo4j.graphdb.Node
import org.reudd.util.ReUddConstants;

public abstract class BaseNode {

	Node underlyingNode

	def id

	def attributes

	Date created

	Date lastUpdate

	/**
	 * Creates a new BaseNode.
	 */
	BaseNode() {
		this.attributes = [:]
	}

	/**
	 * Creates a new BaseNode that already has a underlying node in
	 * the neo service.
	 */
	BaseNode(underlyingNode) {
		this.underlyingNode = underlyingNode
		this.id = underlyingNode.id
		this.attributes = [:]
		for (key in underlyingNode.getPropertyKeys()) {
			if (!key.startsWith(ReUddConstants.PREFIX)) {
				attributes[key] = underlyingNode.getProperty(key)
			}
		}
		def createdDate
		if (underlyingNode.hasProperty(ReUddConstants.CREATED)) {
			def time = underlyingNode.getProperty(ReUddConstants.CREATED)
			createdDate = new Date(time)
		}
		this.created = createdDate
		def updateDate
		if (underlyingNode.hasProperty(ReUddConstants.LAST_UPDATE)) {
			def time = underlyingNode.getProperty(ReUddConstants.LAST_UPDATE)
			updateDate = new Date(time)
		}
		this.lastUpdate = updateDate
	}

	/**
	 * Returns a string representation of the BaseNode.
	 */
	def String toString() {
		if (this.id) {
			def string = "Node-" + this.id + " ["
			for (item in this.attributes) {
				string += item.key + ":" + item.value + ","
			}
			if (string[-1]==",") {
				string = string[0..-2]
			}
			string += "]"
		} else {
			def string = "New BaseNode"
		}
	}

	/**
	 * Checks if tow BaseNodes are equal by comparing id
	 */
	def boolean equals(baseNode) {
		this.id == baseNode.id
	}

}