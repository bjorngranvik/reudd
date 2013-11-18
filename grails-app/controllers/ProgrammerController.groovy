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


import org.neo4j.graphdb.Direction
import org.reudd.node.DataNodeFactory
import org.reudd.node.TypeNodeFactory
import org.reudd.node.ViewNodeFactory
import org.reudd.reports.ReportNodeFactory
import org.reudd.statistics.NodePathBuilder
import org.reudd.util.ReUddConstants
import org.reudd.util.ReUddRelationshipTypes

public class ProgrammerController {

	def graphDatabaseService

	def index = { redirect( action:whatHasHappenedSince, params:params )
	}

	def listTypes = {
		def data = [:]
		TypeNodeFactory factory = new TypeNodeFactory(graphDatabaseService)
		data.allNodes = factory.getTypeNodes()
		data
	}

	def listViews = {
		def data = [:]
		ViewNodeFactory factory = new ViewNodeFactory(graphDatabaseService)
		data.viewNodes = factory.getViewNodes()
		data
	}

	def listReports = {
		def data = [:]
		ReportNodeFactory reportNodeFactory = new ReportNodeFactory(graphDatabaseService)
		data.reportNodes = reportNodeFactory.getReportNodes()
		data
	}

	def editReport = {
		def data = [:]
		ReportNodeFactory reportNodeFactory = new ReportNodeFactory(graphDatabaseService)
		data.report = reportNodeFactory.getReportNode(params.id.toLong())
		data.templates = reportNodeFactory.getReportTemplateNodes()
		data
	}

	def editReportSubmit = {
		ReportNodeFactory reportNodeFactory = new ReportNodeFactory(graphDatabaseService)
		def report = reportNodeFactory.getReportNode(params.id.toLong())
		report.title = params.title
		report.body = params.body
		if (params.newcomment != "") {
			report.addComment params.newcomment, "Observer"
		}
		report.save()
		redirect( action:listReports )
	}

	def showType = {
		def data = [:]
		TypeNodeFactory factory = new TypeNodeFactory(graphDatabaseService)
		def node = factory.getTypeNode(params.id.toLong())
		node.settings = node.settings.replace("\"","&quot;")
		data.node = node
		data.SHOW = true
		render(view:"crudType", model:data)
	}

	def editType = {
		def data = [:]
		TypeNodeFactory factory = new TypeNodeFactory(graphDatabaseService)
		def node = factory.getTypeNode(params.id.toLong())
		node.settings = node.settings.replace("\"","&quot;")
		node.checkExistingAttributes()
		data.node = node
		data.EDIT = true
		render(view:"crudType", model:data)
	}

	def updateType = {
		TypeNodeFactory factory = new TypeNodeFactory(graphDatabaseService)
		def node = factory.getTypeNode(params.id.toLong())
		// set name
		def name = params.name
		node.setName(name)
		// set settings
		def settings = params.settings
		node.settings = settings
		// set attributes
		node.attributes = [:]
		def attributeKeys = params.attributeKeys
		def attributeValues = params.attributeValues
		if (attributeKeys instanceof String) {
			if (!attributeKeys.equals("") && !attributeValues.equals("")) {
				node.attributes[attributeKeys] = attributeValues
			}
		} else {
			attributeKeys.eachWithIndex { key, i ->
				if (!key.equals("") && !attributeValues[i].equals("")) {
					node.attributes[key] = attributeValues[i]
				}
			}
		}
		factory.saveNode(node)
		params.id = node.id
		redirect( action:showType, params:[id:node.id] )
	}

	def deleteType = {
		TypeNodeFactory factory = new TypeNodeFactory(graphDatabaseService)
		def typeNode = factory.getTypeNode(params.id.toLong())
		factory.deleteNode(typeNode)
		redirect( action:listTypes )
	}

	def deleteManyTypes = {
		def selectedIds = []
		if (params.selectedIds instanceof String) {
			selectedIds << params.selectedIds
		} else {
			selectedIds = params.selectedIds
		}
		TypeNodeFactory factory = new TypeNodeFactory(graphDatabaseService)
		selectedIds.each {
			def typeNode = factory.getTypeNode(it.toLong())
			factory.deleteNode(typeNode)
		}
		redirect( action:listTypes )
	}

	def deleteManyReports = {
		def selectedIds = []
		if (params.selectedIds instanceof String) {
			selectedIds << params.selectedIds
		} else {
			selectedIds = params.selectedIds
		}
		ReportNodeFactory factory = new ReportNodeFactory(graphDatabaseService)
		selectedIds.each {
			def typeNode = factory.getReportNode(it.toLong())
			factory.deleteNode(typeNode)
		}
		redirect( action:listReports )
	}

	def deleteManyViews = {
		def selectedIds = []
		if (params.selectedIds instanceof String) {
			selectedIds << params.selectedIds
		} else {
			selectedIds = params.selectedIds
		}
		ViewNodeFactory factory = new ViewNodeFactory(graphDatabaseService)
		selectedIds.each {
			def viewNode = factory.getViewNode(it.toLong())
			viewNode.delete()
		}
		redirect( action:listViews )
	}


	def tagCloudTypes = {
		def data = [:]
		TypeNodeFactory typeFactory = new TypeNodeFactory(graphDatabaseService)
		DataNodeFactory dataFactory = new DataNodeFactory(graphDatabaseService)
		data.typeNodes = typeFactory.getTypeNodes()
		data.totalNbrOfDataNodes = dataFactory.getDataNodes().size()
		data
	}

	def nodeConnections = {

	}

	def nodeConnectionsGraphviz = {
		def dotBuffer = new StringWriter()
		def out = new PrintWriter(dotBuffer)

		TypeNodeFactory typeNodeFactory = new TypeNodeFactory(graphDatabaseService)

		out.println """digraph nodeConnections {"""
		out.println """size="8,20";"""
		out.println """node [shape=circle,fixedsize=true,width=1,height=1];"""

		def typeNodes = typeNodeFactory.getTypeNodes()
		typeNodes.each { type ->
			typeNodes.each { otherType ->
				def percentage = type.getConnectionPercentagesToType(otherType.name)
				if (percentage != 0) {
					def startName = type.name.escapeSomeHtml()
					def endName = otherType.name.escapeSomeHtml()
					out.println """<$startName> -> <$endName> [label="  $percentage%  "]"""
				}
			}
		}
		out.println "}"

		Runtime runtime = Runtime.getRuntime()
		Process p = runtime.exec("dot -Tpng")
		p.outputStream.withStream { stream ->
			stream << dotBuffer.toString()
		}

		def imageBuffer = new ByteArrayOutputStream()
		imageBuffer << p.inputStream
		byte[] image = imageBuffer.toByteArray()

		response.contentLength = image.length
		response.contentType = "image/png"
		response.outputStream << image
	}

	def navigatedPaths = {

	}

	def navigatedPathsGraphviz = {
		def dotBuffer = new StringWriter()
		def out = new PrintWriter(dotBuffer)

		TypeNodeFactory typeNodeFactory = new TypeNodeFactory(graphDatabaseService)

		out.println """digraph navigatedPaths {"""
//		out.println """rankdir="LR";"""
		out.println """size="8,20";"""
		out.println """node [shape=circle,fixedsize=true,width=1,height=1];"""

		NodePathBuilder pathBuilder = new NodePathBuilder(graphDatabaseService)
		def rootNode = pathBuilder.statisticsNode
		def relationships = rootNode.getRelationships(ReUddRelationshipTypes._REUDD_NODE_PATH, Direction.OUTGOING)
		def outCount = 0
		relationships.each { relationship ->
			def travCount = relationship.getProperty(ReUddConstants.NODE_PATH_TRAVERSED_COUNT)
			outCount += travCount
		}
		printRecursiveNavPaths(rootNode, out, true, outCount)
		out.println "}"

		Runtime runtime = Runtime.getRuntime()
		Process p = runtime.exec("dot -Tpng")
		p.outputStream.withStream { stream ->
			stream << dotBuffer.toString()
		}

		def imageBuffer = new ByteArrayOutputStream()
		imageBuffer << p.inputStream
		byte[] image = imageBuffer.toByteArray()

		response.contentLength = image.length
		response.contentType = "image/png"
		response.outputStream << image
	}

	private def printRecursiveNavPaths(node, PrintWriter out, isStartNode, prevCount) {
		def nodeString = isStartNode ? "Start" : "No Type"
		if (node.hasProperty(ReUddConstants.STATISTIC_NODE_PATH_STRING)) {
			nodeString = node.getProperty(ReUddConstants.STATISTIC_NODE_PATH_STRING)
		}
		nodeString = nodeString.replaceAll(", ", "<br/>").escapeSomeHtml()
		out.println """"$node.id" [label=<$nodeString>]"""
		def relationships = node.getRelationships(ReUddRelationshipTypes._REUDD_NODE_PATH, Direction.OUTGOING)
		relationships.each { relationship ->
			def endNode = relationship.endNode
			def travCount = relationship.getProperty(ReUddConstants.NODE_PATH_TRAVERSED_COUNT)
			def percentage = 100
			if (prevCount) {
				percentage = new BigDecimal((travCount / prevCount) * 100)
				percentage = percentage.setScale(0, BigDecimal.ROUND_HALF_UP)
			}
			out.println """"$node.id" -> "$endNode.id" [label="  $percentage% ($travCount)  "]"""
			printRecursiveNavPaths(endNode, out, false, travCount)
		}
	}

	def addView = {
		def data = [:]
		ViewNodeFactory viewNodeFactory = new ViewNodeFactory(graphDatabaseService)
		TypeNodeFactory typeNodeFactory = new TypeNodeFactory(graphDatabaseService)
		def typeNode = typeNodeFactory.getTypeNode(params.typeId.toLong())
		def viewNode = viewNodeFactory.createNode()
		typeNode.addViewNode viewNode
		data.viewNode = viewNode
		redirect(action:editView, params:[id:viewNode.id])
	}

	def editView = {
		def data = [:]
		ViewNodeFactory viewNodeFactory = new ViewNodeFactory(graphDatabaseService)
		data.view = viewNodeFactory.getViewNode(params.id.toLong())
		data
	}

	def editViewSubmit = {
		ViewNodeFactory viewNodeFactory = new ViewNodeFactory(graphDatabaseService)
		def view = viewNodeFactory.getViewNode(params.id.toLong())
		view.body = params.body
		view.coveredAttributes = params.coveredAttributes
		view.save()
		redirect( action:listViews )
	}

	def deleteView = {
		ViewNodeFactory viewNodeFactory = new ViewNodeFactory(graphDatabaseService)
		def view = viewNodeFactory.getViewNode(params.id.toLong())
		view.delete()
		redirect( action:listTypes )
	}

	def whatHasHappenedSince = {
		def data = [:]
		Date date = new Date().minus(1)
		if (params.format && params.date) {
			date = Date.parse(params.format, params.date)
		}
		data.sinceDate = date.format("yyMMdd HH:mm:ss")
		DataNodeFactory dataNodeFactory = new DataNodeFactory(graphDatabaseService)
		TypeNodeFactory typeNodeFactory = new TypeNodeFactory(graphDatabaseService)
		ReportNodeFactory reportNodeFactory = new ReportNodeFactory(graphDatabaseService)
		def allDataNodes = dataNodeFactory.getDataNodes()
		def allTypeNodes = typeNodeFactory.getTypeNodes()
		def allReportNodes = reportNodeFactory.getReportNodes()
		def itemList = []
		allDataNodes.each { node ->
			if (node.lastUpdate > date) {
				itemList << [item:node,type:"Data"]
			}
		}
		allTypeNodes.each { node ->
			if (node.lastUpdate > date) {
				itemList << [item:node,type:"Type"]
			}
		}
		allReportNodes.each { node ->
			if (node.lastUpdate > date) {
				itemList << [item:node,type:"Report"]
			}
		}
		data.itemList = itemList
		data
	}

}
