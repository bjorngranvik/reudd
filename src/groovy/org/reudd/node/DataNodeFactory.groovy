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
import org.neo4j.graphdb.GraphDatabaseService
import org.reudd.util.ReUddRelationshipTypes;

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