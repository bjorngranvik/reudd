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

import org.neo4j.graphdb.*
import org.reudd.util.ReUddConstants;

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