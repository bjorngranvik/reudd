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

import org.neo4j.graphdb.Direction;
import java.util.List;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.reudd.util.ReUddConstants;
import org.reudd.util.ReUddRelationshipTypes;

class ViewNode {
	
	def id
	
	Node underlyingNode
	
	String body
	
	TypeNode type
	
	String coveredAttributes
	
	ViewNode(Node node) {
		this.underlyingNode = node
		this.id = node.id
		if (underlyingNode.hasProperty(ReUddConstants.VIEW_BODY)) {
			this.body = underlyingNode.getProperty(ReUddConstants.VIEW_BODY)
		}
		Relationship typeRelation = underlyingNode.getSingleRelationship(ReUddRelationshipTypes._REUDD_HAS_VIEW, Direction.INCOMING)
		if (typeRelation) {
			this.type = new TypeNode(typeRelation.getStartNode())
		}
		if (underlyingNode.hasProperty(ReUddConstants.COVERED_ATTRIBUTES)) {
			this.coveredAttributes = underlyingNode.getProperty(ReUddConstants.COVERED_ATTRIBUTES)
		}
	}
	
	def coversAttribute(String key) {
		def coveredList = coveredAttributes.split(",")*.trim() as List
		if (!coveredList instanceof String) {
			coveredList = [coveredList]
		}
		if (coveredList.contains(key)) {
			return true
		}
		false
	}
	
	def boolean hasBody() {
		(body && body != "")
	}
	
	def save() {
		underlyingNode.setProperty(ReUddConstants.VIEW_BODY, body)
		underlyingNode.setProperty(ReUddConstants.COVERED_ATTRIBUTES, coveredAttributes)
	}
	
	def delete() {
		underlyingNode.getRelationships().each { Relationship rel ->
			rel.delete()
		}
		underlyingNode.delete()
	}

}