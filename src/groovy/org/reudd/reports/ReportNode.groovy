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
import org.reudd.util.*;
import java.util.Date;

class ReportNode {
	
	final Node underlyingNode
	
	final def id
	
	def title
	
	def body
	
	def comments
	
	Date created
	
	Date lastUpdate
	
	ReportNode(Node node) {
		this.underlyingNode = node
		this.id = node.id
		if (underlyingNode.hasProperty("title")) {
			this.title = underlyingNode.getProperty("title")
		}
		if (underlyingNode.hasProperty("body")) {
			this.body = underlyingNode.getProperty("body")
		}
		if (underlyingNode.hasProperty("comments")) {
			String[] commentStrings = underlyingNode.getProperty("comments")
			this.comments = []
			for (string in commentStrings) {
				def commentList = string.split(ReUddConstants.COMMENT_SEPARATOR)
				def commentMap = [author:commentList[0],date:new Date(commentList[1].toLong()),comment:commentList[2]]
				this.comments += commentMap
			}
		} else {
			this.comments = []
		}
		def createdDate = new Date().parse("d/M/yyyy","14/12/2009")
		if (underlyingNode.hasProperty(ReUddConstants.CREATED)) {
			def time = underlyingNode.getProperty(ReUddConstants.CREATED)
			createdDate = new Date(time)
		}
		this.created = createdDate
		def updateDate = new Date().parse("d/M/yyyy","14/12/2009")
		if (underlyingNode.hasProperty(ReUddConstants.LAST_UPDATE)) {
			def time = underlyingNode.getProperty(ReUddConstants.LAST_UPDATE)
			updateDate = new Date(time)
		}
		this.lastUpdate = updateDate
	}
	
	def getComments() {
		comments.sort{ it.date }.reverse()
	}
	
	def addComment(String comment, String author) {
		def date = new Date()
		def commentMap = [author:author,date:date,comment:comment]
		comments += commentMap
	}
	
	def boolean hasBody() {
		body ? true : false
	}
	
	def save() {
		underlyingNode.setProperty "title", title
		underlyingNode.setProperty "body", body
		String[] commentStrings = new String[comments.size()]
		for (int i=0 ; i<commentStrings.length ; i++) {
			def commentMap = comments[i]
			def string = commentMap.author
			string += ReUddConstants.COMMENT_SEPARATOR
			string += commentMap.date.getTime()
			string += ReUddConstants.COMMENT_SEPARATOR
			string += commentMap.comment
			commentStrings[i] = string
		}
		underlyingNode.setProperty "comments", commentStrings
		if (!underlyingNode.hasProperty(ReUddConstants.CREATED)) {
			underlyingNode.setProperty(ReUddConstants.CREATED, new Date().getTime())
		}
		underlyingNode.setProperty(ReUddConstants.LAST_UPDATE, new Date().getTime())
	}
	
	def String toString() {
		title
	}
	
}