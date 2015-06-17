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

package org.reudd.statistics

import org.neo4j.graphdb.Direction
import org.neo4j.graphdb.GraphDatabaseService
import org.neo4j.graphdb.Node
import org.neo4j.graphdb.Relationship
import org.reudd.util.ReUddConstants
import org.reudd.util.ReUddRelationshipTypes

class NodePathBuilder {
	
	final GraphDatabaseService graphDatabaseService
	
	final Node statisticsNode
	
	NodePathBuilder(graphDatabaseService) {
		this.graphDatabaseService = graphDatabaseService
		
		Relationship rel = graphDatabaseService.getReferenceNode().getSingleRelationship(
				ReUddRelationshipTypes._REUDD_STATISTICS_NODE, Direction.OUTGOING)
		
		if (rel == null) {
			statisticsNode = graphDatabaseService.createNode()
			statisticsNode.setProperty("name", "STATISTICS")
			graphDatabaseService.getReferenceNode().createRelationshipTo(statisticsNode, ReUddRelationshipTypes._REUDD_STATISTICS_NODE)
		} else {
			statisticsNode = rel.getEndNode()
		}
	}
	
	def addToNodePathList(list) {
		def prevNode = statisticsNode
		list.eachWithIndex { item, index -> 
			def nextNodeString = item.getSortedTypeNames()
			def nextNode = getOrCreateSubsequentNodePathNode(prevNode, nextNodeString)
			if (index == list.size()-1) {
				def rel = nextNode.getSingleRelationship(ReUddRelationshipTypes._REUDD_NODE_PATH, Direction.INCOMING)
				def currentCount = rel.getProperty(ReUddConstants.NODE_PATH_TRAVERSED_COUNT)
				rel.setProperty ReUddConstants.NODE_PATH_TRAVERSED_COUNT, currentCount + 1
			}
			prevNode = nextNode
		}
	}
	
	def getSubsequentNodePathNode(prevNode, nextNodeString) {
		nextNodeString = nextNodeString.toLowerCase()
		def node
		def relations = prevNode.getRelationships(ReUddRelationshipTypes._REUDD_NODE_PATH, Direction.OUTGOING)
		for (relation in relations) {
			def endNode = relation.getEndNode()
			def endNodeString = endNode.getProperty(ReUddConstants.STATISTIC_NODE_PATH_STRING).toLowerCase()
			if (nextNodeString.equals(endNodeString)) {
				node = endNode
			}
		}
		node
	}
	
	def getOrCreateSubsequentNodePathNode(prevNode, nextNodeString) {
		def node = getSubsequentNodePathNode(prevNode, nextNodeString)
		if (!node) {
			def newNode = graphDatabaseService.createNode()
			newNode.setProperty ReUddConstants.STATISTIC_NODE_PATH_STRING, nextNodeString
			def rel = prevNode.createRelationshipTo(newNode, ReUddRelationshipTypes._REUDD_NODE_PATH)
			rel.setProperty ReUddConstants.NODE_PATH_TRAVERSED_COUNT, 0
			node = newNode
		}
		node
	}
	
	def getNodePaths() {
		def nodePaths = []
		for (relation in statisticsNode.getRelationships(ReUddRelationshipTypes._REUDD_NODE_PATH, Direction.OUTGOING)) {
			def endNode = relation.getEndNode()
			convertNodesToList(endNode, nodePaths)
		}
		nodePaths
	}
	
	def convertNodesToList(node, list) {
		def prevRel = node.getSingleRelationship(ReUddRelationshipTypes._REUDD_NODE_PATH, Direction.INCOMING)
		def traversalCount = prevRel.getProperty(ReUddConstants.NODE_PATH_TRAVERSED_COUNT)
		def nodeString = node.getProperty(ReUddConstants.STATISTIC_NODE_PATH_STRING)
		def stringToAdd = [traversalCount:traversalCount,name:nodeString]
		list.add(stringToAdd)
		def subList = []
		for (rel in node.getRelationships(ReUddRelationshipTypes._REUDD_NODE_PATH, Direction.OUTGOING)) {
			def endNode = rel.getEndNode()
			convertNodesToList(endNode, subList)
		}
		if (!subList.isEmpty()) { 
			list.add subList
		}
		list
	}
	
	def getPathAsHtmlTable() {
		def html = "<table><tr>"
		for (relation in statisticsNode.getRelationships(ReUddRelationshipTypes._REUDD_NODE_PATH, Direction.OUTGOING)) {
			def endNode = relation.getEndNode()
			html = temp(endNode, html)
		}
		html += "</tr></table>"
		html
	}
	
	private def temp(node, html) {	
		def prevRel = node.getSingleRelationship(ReUddRelationshipTypes._REUDD_NODE_PATH, Direction.INCOMING)
		def traversalCount = prevRel.getProperty(ReUddConstants.NODE_PATH_TRAVERSED_COUNT)
		def nodeString = node.getProperty(ReUddConstants.STATISTIC_NODE_PATH_STRING)
		if (nodeString == "") {
			nodeString = "No Type"
		}
		
		html += "<td>"
		def outgoingRelationships = node.getRelationships(ReUddRelationshipTypes._REUDD_NODE_PATH, Direction.OUTGOING)
		if (outgoingRelationships) {
			html += "<table><tr><td colspan=1000>"
			html += "<div class=\"count\">$traversalCount</div><div class=\"type\">$nodeString</div><div class=\"bottomBorder\"></div>"
			html += "</td></tr>"
			html += "<tr>"
			for (rel in outgoingRelationships) {
				def endNode = rel.getEndNode()
				html = temp(endNode, html)
			}
			html += "</tr></table>"
		} else {
			html += "<table><tr><td>"
			html += "<div class=\"count\">$traversalCount</div><div class=\"type\">$nodeString</div>"
			html += "</td></tr></table>"
		}
		html += "</td>"
		html
	}
	
}