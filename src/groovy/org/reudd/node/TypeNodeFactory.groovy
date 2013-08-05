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