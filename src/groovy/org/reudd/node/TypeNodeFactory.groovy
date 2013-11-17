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

import org.neo4j.graphdb.Direction
import org.neo4j.graphdb.GraphDatabaseService
import org.reudd.util.ReUddConstants
import org.reudd.util.ReUddRelationshipTypes;

public class TypeNodeFactory extends BaseNodeFactory {
	
	TypeNodeFactory(GraphDatabaseService graphDatabaseService) {
		super(graphDatabaseService, "TYPE_NODES", ReUddRelationshipTypes._REUDD_TYPE_NODES, ReUddRelationshipTypes._REUDD_TYPE_NODE)
	}
	
	def TypeNode getOrCreateNode(String typeName) {
		def typeNode = getTypeNode(typeName)
		if (!typeNode) {
			def node = graphDatabaseService.createNode()
			node.setProperty ReUddConstants.TYPE_NAME, typeName
			node.setProperty ReUddConstants.TYPE_SETTINGS, ""
			factoryNode.createRelationshipTo(node, nodeType)
			typeNode = new TypeNode(node)
		}
		typeNode
	}
	
	def TypeNode getTypeNode(typeName) {
		typeName = typeName.toLowerCase()
		def typeNode
		def relations = factoryNode.getRelationships(nodeType, Direction.OUTGOING)
		for (relation in relations) {
			def endNode = relation.getEndNode()
			def endNodeTypeName = endNode.getProperty(ReUddConstants.TYPE_NAME).toLowerCase()
			if (typeName.equals(endNodeTypeName)) {
				typeNode = new TypeNode(endNode)
			}
		}
		typeNode
	}
	
	/**
	 * Saves the TypeNode instance to the neo service.
	 */
	def saveNode(typeNode) {
		super.saveNode(typeNode)
		/*
		 * Save TypeNode name and settings to the underlyingNode.
		 */
		typeNode.underlyingNode.setProperty(ReUddConstants.TYPE_NAME,typeNode.name)
		typeNode.underlyingNode.setProperty(ReUddConstants.TYPE_SETTINGS,typeNode.settings)
		String[] comments = typeNode.comments
		if (comments) {
			typeNode.underlyingNode.setProperty(ReUddConstants.TYPE_COMMENTS,comments)
		}
	}
	
	/**
	 * Returns a list of all TypeNodes currently available
	 */
	def getTypeNodes() {
		def relations = factoryNode.getRelationships(nodeType, Direction.OUTGOING)
		def list = [] 
		for (relation in relations) {
			def typeName = relation.getEndNode().getProperty(ReUddConstants.TYPE_NAME)
			list.add(getTypeNode(typeName))
		}
		list.sort { it.name }
	}
	
	/**
	 * Returns the amount of TypeNodes currently available.
	 */
	def getNbrOfTypeNodes() {
		return getTypeNodes().size()
	}
	
	def connectToType(DataNode dataNode, String name) {
		TypeNode typeNode = createOrGetNode(name)
		dataNode.addType(typeNode)
	}
	
	/**
	 * Retrieves a neo node with the id and returns a TypeNode instance 
	 * created from that node.
	 */
	def getTypeNode(long id) {
		return new TypeNode(graphDatabaseService.getNodeById(id))
	}
	
	def getDataNodesConnectionPercentages() {
		def connectionMap = [:]
		def totalNodesMap = [:]
		def typeNodes = getTypeNodes()
		for (type in typeNodes) {
			connectionMap[type.name] = [:]
			for (t in typeNodes) {
				connectionMap[type.name][t.name] = 0
			}
			def totalDataNodes = type.getAllDataNodes()
			totalNodesMap[type.name] = totalDataNodes.size()
		}
		DataNodeFactory dataNodeFactory = new DataNodeFactory(graphDatabaseService)
		def allDataNodes = dataNodeFactory.getDataNodes()
		for (dataNode in allDataNodes) {
			for (type in typeNodes) {
				if (dataNode.hasOutRelationshipToType(type.name)) {
					for (t in dataNode.types) {
						connectionMap[t.name][type.name]++
					}
				}
			}
		}
		for (item in connectionMap) {
			for (type in connectionMap[item.key]) {
				def nbr = totalNodesMap[item.key]
				if (nbr != 0) {
					BigDecimal bd = new BigDecimal((type.value / nbr) * 100);
					bd = bd.setScale(0, BigDecimal.ROUND_HALF_UP);
					type.value = bd
				}
			}
		}
		connectionMap
	}
	
}