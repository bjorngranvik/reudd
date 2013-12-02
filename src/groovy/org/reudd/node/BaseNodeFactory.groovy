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
import org.neo4j.graphdb.Node
import org.neo4j.graphdb.Relationship
import org.reudd.util.ReUddConstants
import org.reudd.util.ReUddRelationshipTypes

public abstract class BaseNodeFactory {

	final GraphDatabaseService graphDatabaseService

	final Node factoryNode

	final ReUddRelationshipTypes nodeType

    //SMELL: This is required to enable mocking, which in turn should not be needed.
    BaseNodeFactory() {}

	BaseNodeFactory(
            GraphDatabaseService graphDatabaseService, String name,
            ReUddRelationshipTypes relationType, ReUddRelationshipTypes nodeType) {
		this.graphDatabaseService = graphDatabaseService
		this.nodeType = nodeType

		Relationship rel = graphDatabaseService.getReferenceNode().getSingleRelationship(
				relationType, Direction.OUTGOING)

		if (rel == null) {
			factoryNode = graphDatabaseService.createNode()
			factoryNode.setProperty("name", name)
			graphDatabaseService.getReferenceNode().createRelationshipTo(factoryNode, relationType)
		} else {
			factoryNode = rel.getEndNode()
		}
	}

	/**
	 * Saves the BaseNode instance to the neo service. Uses an existing
	 * underlying node if possible, otherwise creates a new and connects
	 * it to the factory node.
	 */
	def saveNode(baseNode) {
		/*
		 * Get or create the underlying node
		 */
		Node underlyingNode
		if (baseNode.underlyingNode) {
			underlyingNode = baseNode.underlyingNode
		} else {
			underlyingNode = graphDatabaseService.createNode()
			factoryNode.createRelationshipTo(underlyingNode, nodeType)
			baseNode.underlyingNode = underlyingNode
			baseNode.id = underlyingNode.id
		}
		/*
		 * Removes all existing properties that are not part of
		 * ReUDD from the BaseNodes underlying neo node.
		 */
		for (key in underlyingNode.getPropertyKeys()) {
			if (!key.startsWith(ReUddConstants.PREFIX)) {
				underlyingNode.removeProperty(key)
			}
		}
		/*
		 * Copy all attribute keys and values from the BaseNode to
		 * the neo node.
		 */
		for (item in baseNode.attributes) {
			underlyingNode.setProperty(item.key, item.value)
		}

		if (!underlyingNode.hasProperty(ReUddConstants.CREATED)) {
			underlyingNode.setProperty(ReUddConstants.CREATED, new Date().getTime())
		}
		underlyingNode.setProperty(ReUddConstants.LAST_UPDATE, new Date().getTime())
	}

	/**
	 * Deletes the underlying node of the BaseNode if it exists
	 */
	def deleteNode(baseNode) {
		if (baseNode.underlyingNode) {
			for (relation in baseNode.underlyingNode.getRelationships()) {
				relation.delete()
			}
			baseNode.underlyingNode.delete()
		}
	}

}
