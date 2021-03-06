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



import groovy.json.JsonOutput
import org.codehaus.groovy.grails.web.pages.GroovyPagesTemplateEngine
import org.neo4j.cypher.javacompat.ExecutionEngine
import org.neo4j.graphdb.GraphDatabaseService
import org.neo4j.graphdb.Transaction
import org.neo4j.kernel.EmbeddedGraphDatabase
import org.reudd.node.BaseNode
import static org.reudd.node.BaseNode.*
import org.reudd.node.DataNodeFactory
import org.reudd.node.DynamicRelationship
import org.reudd.node.TypeNodeFactory
import org.reudd.node.ViewNodeFactory
import org.reudd.reports.ReportNode
import org.reudd.reports.ReportNodeFactory
import org.reudd.statistics.NodePathBuilder
import org.reudd.view.datamodel.D3GraphRendererDataGenerator

public class UserController {

	GraphDatabaseService graphDatabaseService

	GroovyPagesTemplateEngine groovyPagesTemplateEngine

	def beforeExceptions = [
	deleteNode,
	deleteManyNodes,
	getUsedAttributesHtml,
	getUsedRelationshipNamesList,
	updateDynRel,
	addReportSubmit,
	addReportCommentSubmit,
	addTypeCommentSubmit
	]

	def beforeInterceptor = [action:this.&before,except:beforeExceptions]

	def index = {
		ViewNodeFactory vnf = new ViewNodeFactory(graphDatabaseService)
		redirect( action:listNodes, params:params )
	}

	def before = {
		request.dataNodeFactory = new DataNodeFactory(graphDatabaseService)
		request.typeNodeFactory = new TypeNodeFactory(graphDatabaseService)
		request.reportNodeFactory = new ReportNodeFactory(graphDatabaseService)

		def preData = [:]
		preData.menuItemsTypes = request.typeNodeFactory.getTypeNodes()
		preData.menuItemsReports = request.reportNodeFactory.getReportNodes()
		request.preData = preData

		def keepSessionNodePathActions = ["showNode","editNode","updateNode","editDynRel","updateDynRel"]
		if (!(params.action in keepSessionNodePathActions)) {
			invalidateSessionNodePath()
		}

		def keepSessionBreadcrumbsActions = ["showNode","editNode","updateNode","editDynRel","updateDynRel"]
		if (!(params.action in keepSessionBreadcrumbsActions)) {
			invalidateSessionBreadcrumbs()
		}
	}

    def listNodes = {
		def data = request.preData
		DataNodeFactory dataNodeFactory = request.dataNodeFactory
		TypeNodeFactory typeNodeFactory = request.typeNodeFactory
		if (params.type) {
			def type = params.type.decodeURL()
			if (type == "*") {
				data.dataNodes = dataNodeFactory.getDataNodes()
			} else {
				def typeNode = typeNodeFactory.getTypeNode(type)
				data.dataNodes = typeNode.getAllDataNodes()
			}
		}
		data
	}

	def addNode = {
		def data = request.preData
		if (chainModel) {
			data.node = chainModel.node
			data.errorMessages = chainModel.errorMessages
		}
		DataNodeFactory dataNodeFactory = request.dataNodeFactory
		TypeNodeFactory typeNodeFactory = request.typeNodeFactory
		data.dataNodes = dataNodeFactory.getDataNodes()
		data.typeNodes = typeNodeFactory.getTypeNodes()
		data.ADD = true
		render(view:"crudNode", model:data)
	}

	def showNode = {
		def data = request.preData
		DataNodeFactory dataNodefactory = request.dataNodeFactory
		TypeNodeFactory typeNodeFactory = request.typeNodeFactory
		data.node = dataNodefactory.getDataNode(params.id.toLong())
		data.node.increaseTypesViewCount()
		addNodeToSessionNodePath(data.node)
		checkSessionBreadcrumbs(params, data.node)
		data.SHOW = true
		render(view:"crudNode", model:data)
	}

	def invalidateSessionBreadcrumbs() {
		session.breadcrumbs = []
	}

	def checkSessionBreadcrumbs(params, dataNode) {
		if (!session.breadcrumbs) {
			session.breadcrumbs = []
		}
		if (params.crumbIndex) {
			session.breadcrumbs = session.breadcrumbs[0..params.crumbIndex.toLong()]
		} else if (session.breadcrumbs.size() == 0 || session.breadcrumbs[-1] != dataNode) {
			session.breadcrumbs += dataNode
		}
	}

	def invalidateSessionNodePath() {
		session.nodePath = []
	}

	def addNodeToSessionNodePath(node) {
		if (!session.nodePath) {
			session.nodePath = []
		}
		session.nodePath.add(node)
		NodePathBuilder nodePathBuilder = new NodePathBuilder(graphDatabaseService)
		nodePathBuilder.addToNodePathList(session.nodePath)
	}

	def editNode = {
		def data = request.preData
		def node
		DataNodeFactory dataNodeFactory = request.dataNodeFactory
		TypeNodeFactory typeNodeFactory = request.typeNodeFactory
		if (chainModel) {
			node = chainModel.node
			data.errorMessages = chainModel.errorMessages
		} else {
			node = dataNodeFactory.getDataNode(params.id.toLong())
		}
		data.node = node
		node.increaseTypesEditCount()
		def dataNodes = []
		for (item in dataNodeFactory.getDataNodes()) {
			if (item.id != node.id) {
				dataNodes.add(item)
			}
		}
		data.typeNodes = typeNodeFactory.getTypeNodes()
		data.dataNodes = dataNodes
		data.EDIT = true
		render(view:"crudNode", model:data)
	}

	def updateNode = {
		def currentNodeId = params.id
		DataNodeFactory dataNodeFactory = new DataNodeFactory(graphDatabaseService)
		TypeNodeFactory typeNodeFactory = new TypeNodeFactory(graphDatabaseService)
		def node
		if (currentNodeId) {
			node = dataNodeFactory.getDataNode(currentNodeId.toLong())
		} else {
			node = dataNodeFactory.createNode()
		}
		// set tags
		def types = []
		for (tag in params.tagnames.split(",")) {
			tag = tag.trim()
			if (!tag.equals("")) {
				def type = typeNodeFactory.getOrCreateNode(tag.trim())
				types.add type
			}
		}
		node.types = types

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

		// override attributes with the ones from the viewNodes
		def attributes = params.findAll { it.key.length() >= 10 && it.key.startsWith("attribute-")
		}
		attributes.each { key, value ->
			def attrKey = key[key.indexOf("-")+1..-1]
			node.attributes[attrKey] = value
		}


		// set outRelationships
		def outRelationshipList = []
		def outRelationshipNames = params.outRelationshipNames
		def outRelationshipTargets = params.outRelationshipTargets
		if (outRelationshipNames instanceof String) {
			outRelationshipNames = [outRelationshipNames]
		}
		if (outRelationshipTargets instanceof String) {
			outRelationshipTargets = [outRelationshipTargets]
		}
		outRelationshipNames.eachWithIndex { key, i ->
			def targetId = outRelationshipTargets[i]
			def relName = key
			if (!targetId.equals("") && !relName.equals("")) {
				def endNode = dataNodeFactory.getDataNode(targetId)
				outRelationshipList.add(new DynamicRelationship(node, relName, endNode))
			}
		}
		node.outRelationships = outRelationshipList

		// set inRelationships
		def inRelationshipList = []
		def inRelationshipNames = params.inRelationshipNames
		def inRelationshipTargets = params.inRelationshipTargets
		if (inRelationshipNames instanceof String) {
			inRelationshipNames = [inRelationshipNames]
		}
		if (inRelationshipTargets instanceof String) {
			inRelationshipTargets = [inRelationshipTargets]
		}
		inRelationshipNames.eachWithIndex { key, i ->
			def targetId = inRelationshipTargets[i]
			def relName = key
			if (!targetId.equals("") && !relName.equals("")) {
				def startNode = dataNodeFactory.getDataNode(targetId)
				inRelationshipList.add(new DynamicRelationship(startNode, relName, node))
			}
		}
		node.inRelationships = inRelationshipList

		def errorMessages = node.checkForErrors()

		if (!errorMessages.isEmpty()) {
			def data = [:]
			data.errorMessages = errorMessages
			data.node = node
			if (currentNodeId) {
				chain( action:editNode, model:data, params:[id:node.id] )
			} else {
				chain( action:addNode, model:data )
			}
		} else {
			dataNodeFactory.saveNode(node)
			redirect( action:showNode, params:[id:node.id] )
		}
	}

	def deleteNode = {
		def factory = new DataNodeFactory(graphDatabaseService)
		def dataNode = factory.getDataNode(params.id.toLong())
		factory.deleteNode(dataNode)
		redirect( action:index )
	}

	def deleteManyNodes = {
		def selectedIds = []
		if (params.selectedIds instanceof String) {
			selectedIds << params.selectedIds
		} else {
			selectedIds = params.selectedIds
		}
		DataNodeFactory factory = new DataNodeFactory(graphDatabaseService)
		selectedIds.each {
			def dataNode = factory.getDataNode(it.toLong())
			factory.deleteNode(dataNode)
		}
		redirect( action:listNodes, params:[type:params.type] )
	}


    def dataModel = {
      def typeNodeFactory = request.typeNodeFactory
      def data = JsonOutput.toJson(D3GraphRendererDataGenerator.createD3DataModelFromTypeNodesFromTypeNodeFactory(typeNodeFactory))
      render view: "dataModel", model: [data: data,
          menuItemsTypes: request.preData.menuItemsTypes,
          menuItemsReports: request.reportNodeFactory.getReportNodes()]
    }


    def domainModel = {
        def data = request.preData
        data
    }

    def domainModelGraphviz = {
        def dotBuffer = new StringWriter()
        def out = new PrintWriter(dotBuffer)

        TypeNodeFactory typeNodeFactory = new TypeNodeFactory(graphDatabaseService)

        out.println """digraph domainModel {"""
        out.println """size="8,20";"""
        out.println """node [shape=circle,fixedsize=true,width=1,height=1];"""
        def typeNodes = typeNodeFactory.getTypeNodes()
        typeNodes.each { type ->
            def outRels = type.getOutgoingRelationshipNames()
            outRels.each { relName ->
                def targets = type.getOutgoingRelationshipTargetTypeNames(relName)
                targets.each { target ->
                    out.println """<$type.name> -> <$target> [label=<  $relName  >];"""
                }
            }
            if (!outRels) {
                out.println "<$type.name>"
            }
        }
        out.println "}"

        Runtime runtime = Runtime.getRuntime()
        // On a mac and Intellij the PATH variable is not inherited from .bash_profile.
        // This means that Graphviz dot below might work just fine in your Terminal since you PATH variable is setup
        // correctly. However, IntelliJ might not be started with the same values set.
        // Check using
        //    $ launchctl export
        // and see if you have "/usr/local/bin" there.
        // If not, you can use launctl command, create a file in /etc/launchd.conf or fiddle with plists.
        // But these approach all have problems and seem dependent on your Mac OS version. Sigh.
        //
        // Poor man's solution: Set a PATH variable in your IntelliJ project
        // Go Settings->Path Variables and enter a new PATH with for instance the value
        //    /usr/bin:/bin:/usr/sbin:/sbin:/usr/local/bin
        // For some reason I had set the PATH variable on the runtime configuration (various conf for running/debugging).
        Process p = runtime.exec("dot -Tsvg")
        //Process p = runtime.exec("/usr/local/bin/dot -Tpng")
        p.outputStream.withStream { stream ->
            stream << dotBuffer.toString()
        }

        def imageBuffer = new ByteArrayOutputStream()
        imageBuffer << p.inputStream
        byte[] image = imageBuffer.toByteArray()

        response.contentLength = image.length
        response.contentType = "image/svg+xml"
        response.outputStream << image
    }


    def dataModelReport = {
        def data = request.preData
        TypeNodeFactory factory = new TypeNodeFactory(graphDatabaseService)
        data.allNodes = factory.getTypeNodes()
        data
    }


    def importFile = {
        def data = request.preData
      	data
   	}


    def importFileSubmit = {
        def importtext = request.getFile("file").inputStream
        String delimiter = params.delimiter //';'

        importFileSubmitSub(importtext, delimiter, graphDatabaseService)

        redirect( action:index, params:[type:'*'] )
    }

   	def static importFileSubmitSub(importtext, delimiter, graphDatabaseService) {

   		if (importtext) {
   			def nodeList = []
            nodeList = processImportText(importtext, delimiter)

   			// Add nodes
   			DataNodeFactory dataNodeFactory = new DataNodeFactory(graphDatabaseService)
   			TypeNodeFactory typeNodeFactory = new TypeNodeFactory(graphDatabaseService)
   			def savedDataNodes = [:]
   			for (item in nodeList) {
   				def dataNode = dataNodeFactory.createNode()

   				// types
   				for (type in item.types) {
   					dataNode.types.add typeNodeFactory.getOrCreateNode(type)
   				}
   				// attributes
   				dataNode.attributes = item.attributes

   				// save the new node
   				dataNodeFactory.saveNode(dataNode)
   				savedDataNodes.put((item), dataNode)

   			}

   			// Add relationships
   			for (item in nodeList) {
   				def thisNode = savedDataNodes[item]
   				// Updates the incoming relationships to the node
   				thisNode.inRelationships
   				def relationList = []
   				for (rel in item.relationships) {
                    def relDirectionOut = rel.startsWith(BaseNode.RELATIONSHIP_DIRECTION_OUT)
   					def relName = rel[3..rel.lastIndexOf("(")-1]
   					def relTarget = rel[rel.lastIndexOf("(")+1..-2]
   					def relTargetKey = relTarget[0..relTarget.lastIndexOf(":")-1]
   					def relTargetValue = relTarget[relTarget.lastIndexOf(":")+1..-1]
   					for (dataNode in dataNodeFactory.getDataNodes()) {
                        if (dataNode.attributes[relTargetKey] && dataNode.attributes[relTargetKey] == relTargetValue) {
                            if (relDirectionOut) {
                                relationList.add(new DynamicRelationship(thisNode, relName, dataNode))
                            } else {
                                relationList.add(new DynamicRelationship(dataNode, relName, thisNode))
                            }

                        }
   					}
   				}
   				thisNode.outRelationships = relationList
   				dataNodeFactory.saveNode(thisNode)
   			}
   		}
   	}


    static def processImportText(importText, delimiter) {
        def nodeList = []
        def headline = []
        def headLineImported = false

        importText.eachLine { line, index ->
            if ( shouldImportLine(line) ) {
                if (!headLineImported) {
                    line.split(delimiter).each { item ->
                        headline.add item
                    }
                    headLineImported = true
                } else {
                    def newNode = [types: [], attributes: [:], relationships: []]
                    line.split(delimiter).eachWithIndex { item, innerIndex ->
                        if (!item.isEmpty()) {
                            def title = headline[innerIndex]
                            if (title == "type:") {
                                item.split(",").each { type ->
                                    newNode.types.add type.trim()
                                }
                            } else if (title.startsWith(RELATIONSHIP_PREFIX)
                                    || title.startsWith(RELATIONSHIP_DIRECTION_IN_PREFIX)
                                    || title.startsWith(RELATIONSHIP_DIRECTION_OUT_PREFIX)) {
                                def relDirectionOut = !title.startsWith(RELATIONSHIP_DIRECTION_OUT_PREFIX)
                                def relDirection = relDirectionOut ? RELATIONSHIP_DIRECTION_OUT : RELATIONSHIP_DIRECTION_IN
                                def relName = title[4..title.lastIndexOf("(") - 1]
                                def relKey = title[title.lastIndexOf("(") + 1..-2]
                                def relation = "$relDirection$relName($relKey:$item)"
                                newNode.relationships.add relation
                            } else {
                                newNode.attributes[title] = item
                            }
                        }
                    }
                    if (!newNode.isEmpty()) {
                        nodeList.add(newNode)
                    }
                }
            }
        }
        return nodeList
    }


    private static boolean shouldImportLine(line) {
        if (line == "") {
            return false
        }
        return !( isComment(line) || isEmptyLine(line))
    }

    private static boolean isEmptyLine(line) {
        return line.matches("[;]+")
    }

    private static boolean isComment(line) {
        return line.length() >= 2 && line.substring(0, 2) == "//"
    }


    def addReport = {
		def data = request.preData
		data
	}

	def addReportSubmit = {
		ReportNodeFactory reportNodeFactory = request.reportNodeFactory
		ReportNode report
		report = reportNodeFactory.createReportNode()
		report.title = params.title
		report.body = ""
		if (params.newcomment != "") {
			report.addComment(params.newcomment,"User")
		}
		report.save()
		redirect( action:displayReport, params:[id:report.id] )
	}

	def displayReport = {
		def data = request.preData
		TypeNodeFactory typeNodeFactory = request.typeNodeFactory
		DataNodeFactory dataNodeFactory = request.dataNodeFactory
		ReportNodeFactory reportNodeFactory = request.reportNodeFactory
		def report = reportNodeFactory.getReportNode(params.id.toLong())
		data.report = report
		Binding binding = new Binding()
		binding.setProperty("dataNodeFactory", dataNodeFactory)
		binding.setProperty("typeNodeFactory", typeNodeFactory)
        binding.setProperty("engine", new ExecutionEngine( graphDatabaseService ))
		GroovyShell shell = new GroovyShell(binding)
		try {
			data.reportRows = shell.evaluate(report.body)
			data.success = true
		} catch (Exception e) {
			e.printStackTrace()
		}
		data
	}

	def addReportCommentSubmit = {
		ReportNodeFactory reportNodeFactory = new ReportNodeFactory(graphDatabaseService)
		def report = reportNodeFactory.getReportNode(params.id.toLong())
		report.addComment params.newcomment, "User"
		report.save()
		redirect( action:displayReport, params:[id:report.id] )
	}

	def getUsedAttributesHtml = {
		def data = [:]
		def typeList = []
		TypeNodeFactory typeNodeFactory = new TypeNodeFactory(graphDatabaseService)
//		params.types.split(",").each {
        if (params.get("tagnames") != null) {
            params.get("tagnames").split(",").each {
                def typeName = it.trim()
                if (typeName) {
                    def type = typeNodeFactory.getTypeNode(typeName)
                    if (type) {
                        typeList += type
                    }
                }
            }
		}
		def usedAttributes = []
		typeList.each { type ->
			type.checkExistingAttributes()
			type.attributes.each {
				if (!usedAttributes.contains(it.key) && !type.hasViewThatCoversAttribute(it.key)) {
					usedAttributes += it.key
				}
			}
		}
		data.usedAttributes = usedAttributes
		data
	}

	def getUsedRelationshipsHtml = {
		def data = [:]
		def typeList = []
		TypeNodeFactory typeNodeFactory = new TypeNodeFactory(graphDatabaseService)

		//params.types.split(",").each
        if (params.get("tagnames") != null) {
            params.get("tagnames").split(",").each {
                def typeName = it.trim()
                if (typeName) {
                    def type = typeNodeFactory.getTypeNode(typeName)
                    if (type) {
                        typeList += type
                    }
                }
            }
        }
        def usedOutRelationships = []
		def usedInRelationships = []
		typeList.each { type ->
			type.getOutgoingRelationshipNames().each {
				if (!usedOutRelationships.contains(it)) {
					usedOutRelationships += it
				}
			}
			type.getIncomingRelationshipNames().each {
				if (!usedInRelationships.contains(it)) {
					usedInRelationships += it
				}
			}
		}
		data.usedOutRelationships = usedOutRelationships
		data.usedInRelationships = usedInRelationships
		DataNodeFactory dataNodeFactory = new DataNodeFactory(graphDatabaseService)
		data.dataNodes = dataNodeFactory.getDataNodes()
		data
	}

	def getUsedRelationshipNamesList = {
		DataNodeFactory dataNodeFactory = new DataNodeFactory(graphDatabaseService)
		def allDataNodes = dataNodeFactory.getDataNodes()
		def relationshipNames = []
		allDataNodes.each { node ->
			node.getUsedRelationshipNames().each { name ->
				if (!relationshipNames.contains(name)) {
					relationshipNames += name
				}
			}
		}
		def result = ""
		relationshipNames.sort { it	}
		def query = params.q
		def queryLength = query.length()
		relationshipNames.each {
			def nameLength = it.length()
			def startOfString = it[0..(nameLength > queryLength ? queryLength-1 : nameLength-1)]
			if (startOfString.toLowerCase() == query) {
				result += "$it\n"
			} else if (queryLength == 1) {
				result += "$it\n"
			}
		}
		render result
	}

	def editDynRel = {
		def data = request.preData
		DataNodeFactory dataNodeFactory = request.dataNodeFactory
		TypeNodeFactory typeNodeFactory = request.typeNodeFactory
		data.dataNodes = dataNodeFactory.getDataNodes()
		def dynamicRelationship = new DynamicRelationship(graphDatabaseService.getRelationshipById(params.id.toLong()))
		data.dynamicRelationship = dynamicRelationship
		data
	}

	def updateDynRel = {
		DataNodeFactory dataNodeFactory = new DataNodeFactory(graphDatabaseService)
		def dynamicRelationship = new DynamicRelationship(graphDatabaseService.getRelationshipById(params.id.toLong()))

		dynamicRelationship.attributes = [:]
		def attributeKeys = params.attributeKeys
		def attributeValues = params.attributeValues
		if (attributeKeys instanceof String) {
			if (!attributeKeys.equals("") && !attributeValues.equals("")) {
				dynamicRelationship.attributes[attributeKeys] = attributeValues
			}
		} else {
			attributeKeys.eachWithIndex { key, i ->
				if (!key.equals("") && !attributeValues[i].equals("")) {
					dynamicRelationship.attributes[key] = attributeValues[i]
				}
			}
		}

		dynamicRelationship.saveToGraphDatabaseService graphDatabaseService
		redirect( action:'editNode', params:[id:params.nodeId])
	}

	def search = {
		def data = request.preData
		def searchString = params.searchString
		if (!searchString) {
			redirect( action:listNodes, params[type='*'] )
		} else {
			DataNodeFactory dataNodeFactory = request.dataNodeFactory
			data.searchString = searchString
			data.dataNodes = dataNodeFactory.bruteSearch(searchString)
			render( view:'listNodes', model:data )
		}
	}

	def addTypeCommentSubmit = {
		TypeNodeFactory typeNodeFactory = new TypeNodeFactory(graphDatabaseService)
		def status = "FAILURE"
		def typeString = params.types
		def text = params.text
		if (typeString && text) {
			try {
				def typeStringList = typeString.trim().split(",")*.trim()
				def types = []
				typeStringList.each { typeName ->
					types += typeNodeFactory.getTypeNode(typeName)
				}
				types.each { type ->
					type.addComment(text)
					typeNodeFactory.saveNode(type)
				}
				status = "OK"
			} catch (Exception e) {
				e.printStackTrace()
			}
		}
		render( status )
	}

	def renderViewBody = {
		ViewNodeFactory viewNodeFactory = new ViewNodeFactory(graphDatabaseService)
		DataNodeFactory dataNodeFactory = new DataNodeFactory(graphDatabaseService)
		def view = viewNodeFactory.getViewNode(params.viewId.toLong())
		def node = dataNodeFactory.getDataNode(params.nodeId.toLong())
		def edit = params.edit

		def out = new StringWriter()
		def binding = [node:node, edit:edit]
		def engine = groovyPagesTemplateEngine
		def template = engine.createTemplate(view.body,"tempfile").make(binding).writeTo(out)
		render(template)
	}


    def hello() {
        DataNodeFactory dataNodeFactory = new DataNodeFactory(graphDatabaseService)

        render "hello"
    }
}
