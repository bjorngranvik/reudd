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

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Relationship;
import org.reudd.util.ReUddRelationshipTypes;

public class ViewNodeFactory {
	
	GraphDatabaseService graphDatabaseService
	
	Node factoryNode
	
	final ReUddRelationshipTypes nodeRelationType
	
	final ReUddRelationshipTypes rootRelationType
	
	ViewNodeFactory(GraphDatabaseService graphDatabaseService) {
		this.graphDatabaseService = graphDatabaseService
		this.nodeRelationType = ReUddRelationshipTypes._REUDD_VIEW_NODE
		this.rootRelationType = ReUddRelationshipTypes._REUDD_VIEW_NODES
		
		Relationship rel = graphDatabaseService.getReferenceNode().getSingleRelationship(
				rootRelationType, Direction.OUTGOING)
		
		if (rel == null) {
			factoryNode = graphDatabaseService.createNode()
			factoryNode.setProperty("name", "VIEW_NODES")
			graphDatabaseService.getReferenceNode().createRelationshipTo(factoryNode, rootRelationType)
		} else {
			factoryNode = rel.getEndNode()
		}
	}
	
	def ViewNode createNode() {
		def neoNode = graphDatabaseService.createNode()
		factoryNode.createRelationshipTo(neoNode, nodeRelationType)
		new ViewNode(neoNode)
	}
	
	def ViewNode getViewNode(Node node) {
		def viewNode
		def relationshipToViewRoot = node.getSingleRelationship(nodeRelationType, Direction.INCOMING)
		if (relationshipToViewRoot) {
			viewNode = new ViewNode(node)
		}
		viewNode
	}
	
	def ViewNode getViewNode(long id) {
		def neoNode = graphDatabaseService.getNodeById(id)
		getViewNode neoNode
	}
	
	def getViewNodes() {
		def relations = factoryNode.getRelationships(nodeRelationType, Direction.OUTGOING)
		def list = [] 
		for (relation in relations) {
			list.add(new ViewNode(relation.getEndNode()))
		}
		list.sort { it.type.name }
	}
	
}