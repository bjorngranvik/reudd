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

package org.reudd.node;

import org.reudd.util.*;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Node;

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