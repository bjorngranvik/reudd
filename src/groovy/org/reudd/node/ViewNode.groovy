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
import org.neo4j.graphdb.Node
import org.neo4j.graphdb.Relationship
import org.reudd.util.ReUddConstants
import org.reudd.util.ReUddRelationshipTypes

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