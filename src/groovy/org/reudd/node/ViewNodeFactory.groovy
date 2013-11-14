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