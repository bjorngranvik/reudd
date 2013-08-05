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

public class DataNodeFactory extends BaseNodeFactory {
	
	
	DataNodeFactory(GraphDatabaseService graphDatabaseService) {
		super(graphDatabaseService, "DATA_NODES", ReUddRelationshipTypes._REUDD_DATA_NODES, ReUddRelationshipTypes._REUDD_DATA_NODE)
	}
	
	def createNode() {
		new DataNode()
	}
	
	def getDataNode(id) {
		new DataNode(graphDatabaseService.getNodeById(id.toLong()))
	}
	
	def getDataNodes() {
		def relations = factoryNode.getRelationships(nodeType, Direction.OUTGOING)
		def list = []
		for (relation in relations) {
			def id = relation.getEndNode().id
			list.add(getDataNode(id))
		}
		return list
	}
	
	def getNbrOfDataNodes() {
		return getDataNodes().size()
	}
	
	/**
	 * Saves the DataNode instance to the neo service.
	 */
	def saveNode(dataNode) {
		super.saveNode(dataNode)
		/*
		 * Remove old relationships to TypeNodes and create new ones
		 * to the current types  
		 */
		for (relation in dataNode.underlyingNode.getRelationships(ReUddRelationshipTypes._REUDD_IS_OF_TYPE,Direction.OUTGOING)) {
			relation.delete()
		}
		for (typeNode in dataNode.types) {
			dataNode.underlyingNode.createRelationshipTo(typeNode.underlyingNode, ReUddRelationshipTypes._REUDD_IS_OF_TYPE)
		}
		// Delete old DynamicRelationships
		dataNode.deleteUnwantedRelationshipsFromUnderlyingNode()
		/*
		 * Create new relationships to other DataNodes
		 */
		dataNode.getOutRelationships()*.saveToGraphDatabaseService(graphDatabaseService)
		/*
		 * Create new relationships from other DataNodes
		 */
		dataNode.getInRelationships()*.saveToGraphDatabaseService(graphDatabaseService)
	}
	
	def bruteSearch(String searchString) {
		def result = []
		def allNodes = getDataNodes()
		searchString = searchString.toLowerCase()
		allNodes.each { node ->
			node.attributes.each { attribute ->
				def key = attribute.key.toLowerCase()
				def value = attribute.value.toLowerCase()
				if (key.contains(searchString) || value.contains(searchString)) {
					if (!result.contains(node)) {
						result += node
					}
				}
			}
		}
		result
	}
	
}