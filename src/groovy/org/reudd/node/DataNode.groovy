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

package org.reudd.node

import org.neo4j.graphdb.Direction
import org.reudd.util.ReUddRelationshipTypes;

public class DataNode extends BaseNode {
	
	def types
	
	def inRelationships
	
	def outRelationships
	
	DataNode() {
		super()
		types = []
		inRelationships = []
		outRelationships = []
	}
	
	DataNode(underlyingNode) {
		super(underlyingNode)
		types = []
		inRelationships = []
		outRelationships = []
		types = getTypeNodes()
	}
	
	def addType(typeNode) {
		types.add typeNode.name
	}
	
	def getTypeNodes() {
		def typeList = []
		for (relation in underlyingNode.getRelationships(ReUddRelationshipTypes._REUDD_IS_OF_TYPE, Direction.OUTGOING)) {
			typeList.add new TypeNode(relation.getEndNode())
		}
		typeList
	}
	
	def getInRelationships() {
		if (inRelationships == [] && underlyingNode) {
			for (relation in underlyingNode.getDynamicRelationships(Direction.INCOMING)) {
				inRelationships.add new DynamicRelationship(relation)
			}
		}
		inRelationships
	}
	
	def getOutRelationships() {
		if (outRelationships == [] && underlyingNode) {
			for (relation in underlyingNode.getDynamicRelationships(Direction.OUTGOING)) {
				outRelationships.add new DynamicRelationship(relation)
			}
		}
		outRelationships
	}
	
	def getRelationships(String name, Direction direction) {
		def outRelationshipList = []
		if (underlyingNode) {
			for (relation in underlyingNode.getDynamicRelationships(name, direction)) {
				outRelationshipList.add new DynamicRelationship(relation)
			}
		}
		outRelationshipList
	}
	
	def checkForErrors() {
		def errors = []
		for (type in types) {
			/*
			 *  check that required attributes exist
			 */
			def requiredAttributes = type.getRequiredAttributes()
			for (attr in requiredAttributes) {
				if (!attributes[attr]) {
					errors.add("Type " + type.name + " requires the attribute " + attr)
				}
			}
		}
		errors
	}
	
	def String toString() {
		if (underlyingNode) {
			def name = ""
			def toStringList = []
			for (type in getTypeNodes()) {
				def toStringSetting = type.getSetting("toString")
				if (toStringSetting) {
					for (string in toStringSetting) {
						if (string[0]=="\"" && string[-1]=="\"") {
							toStringList.add(string[1..-2])
						} else if (attributes[string]) {
							toStringList.add(attributes[string])
						}
					}
				}
			}
			if (toStringList.isEmpty()) {
				types.each {
					name += it.name
					name += ","
				}
				if (name.endsWith(",")) {
					name = name[0..-2]
					name += ":"
				}
				name += "["
				for (item in this.attributes) {
					name += item.key + ":" + item.value + ","
				}
				if (name[-1]==",") {
					name = name[0..-2]
				}
				name += "]"
			}
			for (string in toStringList) {
				name += string
			}
			name = name.trim()
			if (!name.equals("")) {
				return name
			}
		} else {
			super.toString()
		}
	}
	
	def getConnectedDataNodes() {
		def nodeList = []
		for (relation in underlyingNode.getDynamicRelationships()) {
			nodeList.add new DataNode(relation.getOtherNode(underlyingNode))
		}
		nodeList
	}
	
	def hasOutRelationshipToType(name) {
		for (rel in getOutRelationships()) {
			if (rel.endNode.hasType(name)) {
				return true
			}
		}
		false
	}
	
	def hasType(name) {
		for (type in types) {
			if (type.name.equals(name)) {
				return true
			}
		}
		false
	}
	
	def getSortedTypeNames() {
		def list = []
		def names = ""
		for (type in types) {
			list.add type.name
		}
		for (string in list.sort()) {
			names += string
			names += ", "
		}
		if (names.length() >= 2) {
			names = names[0..-3]
		}
		names
	}
	
	def deleteUnwantedRelationshipsFromUnderlyingNode() {
		def wantedRelationshipsIds = []
		for (relationship in inRelationships) {
			if (relationship.id) {
				wantedRelationshipsIds += relationship.id
			}
		}
		for (relationship in outRelationships) {
			if (relationship.id) {
				wantedRelationshipsIds += relationship.id
			}
		}
		for (relationship in underlyingNode.getDynamicRelationships()) {
			if (!(relationship.id in wantedRelationshipsIds)) {
				relationship.delete()
			}
		}
	}
	
	def getRelationshipWith(endNode, name, direction) {
		if (underlyingNode) {
			for (relationship in underlyingNode.getDynamicRelationships(direction)) {
				if (relationship.getType().name() == name && relationship.getOtherNode(underlyingNode) == endNode.underlyingNode) {
					return relationship
				}
			}
		}
	}
	
	def hasDynamicRelationshipWith(DataNode otherNode, String relName) {
		for (relation in underlyingNode.getDynamicRelationships()) {
			if (relation.getOtherNode(this.underlyingNode) == otherNode.underlyingNode && relation.getType().name() == relName) {
				return true
			}
		}
	}
	
	def getDynamicRelationshipWith(otherNode, relName) {
		for (relation in underlyingNode.getDynamicRelationships()) {
			if (relation.getOtherNode(this.underlyingNode) == otherNode.underlyingNode && relation.getType().name() == relName) {
				return new DynamicRelationship(relation)
			}
		}
	}
	
	def getUsedRelationshipNames() {
		def usedRelationshipNames = []
		getOutRelationships().each { relationship ->
			def relName = relationship.name
			if (!usedRelationshipNames.contains(relName)) {
				usedRelationshipNames += relName
			}
		}
		usedRelationshipNames
	}
	
	def increaseTypesViewCount() {
		for (type in getTypeNodes()) {
			type.increaseViewCount()
		}
	}
	
	def increaseTypesEditCount() {
		for (type in getTypeNodes()) {
			type.increaseEditCount()
		}
	}
	
	def hasViewThatCoversAttribute(String key) {
		def result = false
		types.each { type ->
			if (type.hasViewThatCoversAttribute(key)) {
				result =  true
				return
			}
		}
		result
	}
	
}