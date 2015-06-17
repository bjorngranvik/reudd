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

package org.reudd.reports

import org.neo4j.graphdb.Direction
import org.neo4j.graphdb.GraphDatabaseService
import org.neo4j.graphdb.Node
import org.neo4j.graphdb.Relationship
import org.reudd.util.ReUddRelationshipTypes;

class ReportNodeFactory {
	
	final GraphDatabaseService graphDatabaseService
	
	final Node factoryNode
	
	ReportNodeFactory(GraphDatabaseService graphDatabaseService) {
		this.graphDatabaseService = graphDatabaseService
		
		Relationship rel = graphDatabaseService.getReferenceNode().getSingleRelationship(
		ReUddRelationshipTypes._REUDD_REPORT_NODES, Direction.OUTGOING)
		
		if (rel == null) {
			factoryNode = graphDatabaseService.createNode()
			factoryNode.setProperty("name", "REPORTS")
			graphDatabaseService.getReferenceNode().createRelationshipTo(factoryNode, ReUddRelationshipTypes._REUDD_REPORT_NODES)
		} else {
			factoryNode = rel.getEndNode()
		}
	}
	
	def ReportNode createReportNode() {
		Node node = graphDatabaseService.createNode()
		factoryNode.createRelationshipTo(node, ReUddRelationshipTypes._REUDD_REPORT_NODE)
		new ReportNode(node)
	}
	
	def ReportNodeTemplate getOrCreateReportNodeTemplate(String title) {
		def reportTemplateNode = getReportNodeTemplate(title)
		if (!reportTemplateNode) {
			Node node = graphDatabaseService.createNode()
			factoryNode.createRelationshipTo(node, ReUddRelationshipTypes._REUDD_REPORT_TEMPLATE)
			reportTemplateNode = new ReportNodeTemplate(node)
		}
		reportTemplateNode
	}
	
	def ReportNodeTemplate getReportNodeTemplate(String title) {
		def result = null
		for (Relationship rel in factoryNode.getRelationships(ReUddRelationshipTypes._REUDD_REPORT_TEMPLATE, Direction.OUTGOING)) {
			def endNode = rel.getEndNode()
			if (endNode.hasProperty("title") && endNode.getProperty("title") == title) {
				result = new ReportNodeTemplate(endNode)
			}
		}
		result
	}
	
	def ReportNode getReportNode(long id) {
		new ReportNode(graphDatabaseService.getNodeById(id))
	}
	
	def getReportNodes() {
		def relations = factoryNode.getRelationships(ReUddRelationshipTypes._REUDD_REPORT_NODE, Direction.OUTGOING)
		def list = []
		for (relation in relations) {
			def reportNode = new ReportNode(relation.getEndNode())
			list.add(reportNode)
		}
		return list
	}
	
	def getReportTemplateNodes() {
		def relations = factoryNode.getRelationships(ReUddRelationshipTypes._REUDD_REPORT_TEMPLATE, Direction.OUTGOING)
		def list = []
		for (relation in relations) {
			def reportNodeTemplate = new ReportNodeTemplate(relation.getEndNode())
			list.add(reportNodeTemplate)
		}
		return list
	}
	
	def deleteNode(reportNode) {
		if (reportNode.underlyingNode) {
			for (relation in reportNode.underlyingNode.getRelationships()) {
				relation.delete()
			}
			reportNode.underlyingNode.delete()
		}
	}
	
}