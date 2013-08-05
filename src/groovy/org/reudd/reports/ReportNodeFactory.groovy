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

package org.reudd.reports;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Direction;
import org.reudd.util.*;

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