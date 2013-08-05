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

import java.util.Date;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Node;
import org.reudd.util.*;
import org.neo4j.graphdb.Direction;

public abstract class BaseNodeFactory {
	
	final GraphDatabaseService graphDatabaseService
	
	final Node factoryNode
	
	final ReUddRelationshipTypes nodeType
	
	BaseNodeFactory(GraphDatabaseService graphDatabaseService, String name, ReUddRelationshipTypes relationType, ReUddRelationshipTypes nodeType) {
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