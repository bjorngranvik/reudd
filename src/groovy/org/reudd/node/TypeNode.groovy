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

import org.reudd.util.*;
import org.neo4j.graphdb.*;

public class TypeNode extends BaseNode {
	
	def name
	
	def settings
	
	def comments
	
	/**
	 * Creates a TypeNode from an existing neo node and sets name and
	 * settings from the node content.
	 */
	TypeNode(underlyingNode) {
		super(underlyingNode)
		if (underlyingNode.hasProperty(ReUddConstants.TYPE_NAME)) {
			name = underlyingNode.getProperty(ReUddConstants.TYPE_NAME)
		}
		if (underlyingNode.hasProperty(ReUddConstants.TYPE_SETTINGS)) {
			settings = underlyingNode.getProperty(ReUddConstants.TYPE_SETTINGS)
		}
		if (underlyingNode.hasProperty(ReUddConstants.TYPE_COMMENTS)) {
			String[] tempComments = underlyingNode.getProperty(ReUddConstants.TYPE_COMMENTS)
			comments = []
			for (comment in tempComments) {
				comments += comment
			}
		}
		if (!underlyingNode.hasProperty(ReUddConstants.TYPE_VIEW_COUNT)) {
			underlyingNode.setProperty(ReUddConstants.TYPE_VIEW_COUNT,0)
		}
		if (!underlyingNode.hasProperty(ReUddConstants.TYPE_EDIT_COUNT)) {
			underlyingNode.setProperty(ReUddConstants.TYPE_EDIT_COUNT,0)
		}
	}
	
	def getSetting(settingName) {
		if (settings && !settings?.isEmpty()) {
			SettingsParser.getSetting(settingName, settings)
		}
	}
	
	def getRequiredAttributes() {
		def reqAttr = []
		for (attr in attributes) {
			if (attr.value && !attr.value.isEmpty() && SettingsParser.getAttributeSetting("required",attr.value)) {
				reqAttr.add(attr.key)
			}
		}
		reqAttr
	}
	
	/**
	 * Returns a string representation of the TypeNode
	 */
	def String toString() {
		"TypeNode-" + name
	}
	
	/**
	 * Calculates the percentage of totalNbr of DataNodes that have the
	 * current TypeNode as one of it's types
	 */
	def calcPercentage(totalNbr) {
		BigDecimal bd = new BigDecimal((countDataNodes() / totalNbr) * 100);
		bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
		return bd
	}
	
	/**
	 * Returns a list of all DataNodes that have the current TypeNode as one
	 * of it's types
	 */
	def getAllDataNodes() {
		def relationships = underlyingNode.getRelationships(ReUddRelationshipTypes._REUDD_IS_OF_TYPE, Direction.INCOMING)
		def dataNodes = []
		for (relation in relationships) {
			def node = relation.getStartNode()
			dataNodes.add(new DataNode(node))
		}
		dataNodes
	}
	
	/**
	 * Returns the amount of DataNodes that have the current TypeNode as one
	 * of it's types
	 */
	def countDataNodes() {
		underlyingNode.getRelationships(ReUddRelationshipTypes._REUDD_IS_OF_TYPE, Direction.INCOMING).size()
	}
	
	def calcTotalOutgoingRelations() {
		def total = 0
		for (node in getAllDataNodes()) {
			total += node.outRelationships.size()
		}
		total
	}
	
	def calcAverageOutgoingRelations() {
		def nbrOfDataNodes = countDataNodes()
		if (nbrOfDataNodes == 0) {
			return 0
		}
		BigDecimal bd = new BigDecimal((calcTotalOutgoingRelations() / nbrOfDataNodes));
		bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
	}
	
	def calcTotalIncomingRelations() {
		def total = 0
		for (node in getAllDataNodes()) {
			total += node.inRelationships.size()
		}
		total
	}
	
	def calcAverageIncomingRelations() {
		def nbrOfDataNodes = countDataNodes()
		if (nbrOfDataNodes == 0) {
			return 0
		}
		BigDecimal bd = new BigDecimal((calcTotalIncomingRelations() / nbrOfDataNodes));
		bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
	}
	
	/**
	 * Checks if the TypeNode has any kind of meta-information by looking 
	 * at it's settings and attributes.
	 */
	def hasMetaModel() {
		def hasSettings = (settings != null && settings.size() > 0)
		def hasAttributes = (attributes != null && !attributes.isEmpty())
		hasSettings || hasAttributes
	}
	
	def getConnectionPercentagesToType(String otherTypeName) {
		def allNodes = getAllDataNodes()
		def totalNbrOfNodes = allNodes.size()
		def foundConnections = 0
		for (dataNode in allNodes) {
			for (connectedNode in dataNode.getConnectedDataNodes()) {
				if (otherTypeName in connectedNode.types*.name) {
					foundConnections += 1
					break
				}
			}
		}
		BigDecimal bd = 100;
        if (totalNbrOfNodes != 0) {
            bd = new BigDecimal((foundConnections / totalNbrOfNodes) * 100)
        }
		bd = bd.setScale(0, BigDecimal.ROUND_HALF_UP)
	}
	
	private def getAllTypeNodes() {
		def rel = underlyingNode.getSingleRelationship(ReUddRelationshipTypes._REUDD_TYPE_NODE, Direction.INCOMING)
		def factoryNode = rel.getStartNode()
		def relations = factoryNode.getRelationships(ReUddRelationshipTypes._REUDD_TYPE_NODE, Direction.OUTGOING)
		def list = []
		for (relation in relations) {
			def endNode = relation.getEndNode()
			list.add(new TypeNode(endNode))
		}
		return list
	}
	
	def getOutgoingRelationshipNames() {
		def names = []
		def dataNodes = getAllDataNodes()
		dataNodes.each { dataNode ->
			dataNode.outRelationships.each {
				if (!names.contains(it.name)) {
					names += it.name
				}
			}
		}
		names
	}
	
	def getIncomingRelationshipNames() {
		def names = []
		def dataNodes = getAllDataNodes()
		dataNodes.each { dataNode ->
			dataNode.inRelationships.each {
				if (!names.contains(it.name)) {
					names += it.name
				}
			}
		}
		names
	}
	
	def getOutgoingRelationshipTargetTypeNames(String relName) {
		def names = []
		def dataNodes = getAllDataNodes()
		dataNodes.each { dataNode ->
			dataNode.getRelationships(relName,Direction.OUTGOING).each { relationship ->
				relationship.endNode.types.each { type ->
					if (!names.contains(type.name)) {
						names += type.name
					}
				}
			}
		}
		names
	}
	
	def checkExistingAttributes() {
		def dataNodes = getAllDataNodes()
		dataNodes.each { node ->
			node.attributes.each { attribute ->
				if (!attributes[attribute.key]) {
					attributes[attribute.key] = ""
				}
			}
		}
	}
	
	def increaseViewCount() {
		def currentCount = underlyingNode.getProperty(ReUddConstants.TYPE_VIEW_COUNT)
		underlyingNode.setProperty(ReUddConstants.TYPE_VIEW_COUNT, currentCount+1)
	}
	
	def increaseEditCount() {
		def currentCount = underlyingNode.getProperty(ReUddConstants.TYPE_EDIT_COUNT)
		underlyingNode.setProperty(ReUddConstants.TYPE_EDIT_COUNT, currentCount+1)
	}
	
	def getViewCount() {
		underlyingNode.getProperty(ReUddConstants.TYPE_VIEW_COUNT)
	}
	
	def getEditCount() {
		underlyingNode.getProperty(ReUddConstants.TYPE_EDIT_COUNT)
	}
	
	def addComment(String text) {
		if (comments) {
			comments += text
		} else {
			comments = [text]
		}
	}
	
	def hasView() {
		def result = false
		def viewRelation = underlyingNode.getSingleRelationship(ReUddRelationshipTypes._REUDD_HAS_VIEW, Direction.OUTGOING)
		if (viewRelation) {
			result = true
		}
		result
	}
	
	def addViewNode(ViewNode viewNode) {
		underlyingNode.createRelationshipTo(viewNode.underlyingNode, ReUddRelationshipTypes._REUDD_HAS_VIEW)
	}
	
	def ViewNode getViewNode() {
		def viewNode
		def viewRelation = underlyingNode.getSingleRelationship(ReUddRelationshipTypes._REUDD_HAS_VIEW, Direction.OUTGOING)
		if (viewRelation) {
			viewNode = new ViewNode(viewRelation.getEndNode())
		}
		viewNode
	}
	
	def hasViewThatCoversAttribute(String key) {
		if (getViewNode()?.coversAttribute(key)) {
			return true
		}
		false
	}
	
}