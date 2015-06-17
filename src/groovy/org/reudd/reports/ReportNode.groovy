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

import org.neo4j.graphdb.Node
import org.reudd.util.ReUddConstants

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