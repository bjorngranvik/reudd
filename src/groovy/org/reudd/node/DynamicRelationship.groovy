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

import org.neo4j.graphdb.*;
import org.reudd.util.*;

public class DynamicRelationship {
	
	Relationship underlyingRelationship
	
	def id
	
	def name
	
	DataNode startNode
	
	DataNode endNode
	
	def attributes
	
	DynamicRelationship(DataNode startNode, String name, DataNode endNode) {
		def relationship = startNode.getRelationshipWith(endNode,name,Direction.OUTGOING)
		if (relationship) {
			initFromRelationship(relationship)
		} else {
			this.name = name
			this.startNode = startNode
			this.endNode = endNode
			attributes = [:]
		}
	}
	
	DynamicRelationship(Relationship relationship) {
		initFromRelationship(relationship)
	}
	
	private def initFromRelationship(Relationship relationship) {
		this.underlyingRelationship = relationship
		this.id = relationship.id
		this.name = relationship.getType().name()
		this.startNode = new DataNode(relationship.getStartNode())
		this.endNode = new DataNode(relationship.getEndNode())
		attributes = [:]
		for (key in relationship.getPropertyKeys()) {
			if (!key.startsWith(ReUddConstants.PREFIX)) {
				attributes[key] = relationship.getProperty(key)
			}
		}
	}
	
	def getOtherNode(DataNode otherNode) {
		startNode == otherNode ? endNode : startNode
	}
	
	def String toString() {
		"$startNode $name $endNode"
	}
	
	def boolean equals(baseNode) {
		this.id == baseNode.id
	}
	
	def saveToGraphDatabaseService(GraphDatabaseService neo) {
		if (!underlyingRelationship) {
			Node startNeoNode = neo.getNodeById(startNode.id)
			Node endNeoNode = neo.getNodeById(endNode.id)
underlyingRelationship = startNeoNode.createRelationshipTo(endNeoNode, DynamicRelationshipType.withName(name))
		} else {
			if (startNode.underlyingNode == underlyingRelationship.getStartNode() && endNode.underlyingNode == underlyingRelationship.getEndNode()) {
				if (underlyingRelationship.type.name != name) {
					underlyingRelationship.delete()
					underlyingRelationship = startNode.underlyingNode.createRelationshipTo(endNode.underlyingNode, DynamicRelationshipType.withName(name))
				}
			} else {
				underlyingRelationship.delete()
				underlyingRelationship = startNode.underlyingNode.createRelationshipTo(endNode.underlyingNode, DynamicRelationshipType.withName(name))
			}
			id = underlyingRelationship.id
		}
		for (key in underlyingRelationship.getPropertyKeys()) {
			underlyingRelationship.removeProperty(key)
		}
		this.attributes.each { key, value ->
			underlyingRelationship.setProperty(key, value)
		}
	}
	
}