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

package org.reudd

import org.neo4j.graphdb.Node
import org.neo4j.graphdb.Direction
import org.neo4j.graphdb.GraphDatabaseService
import org.neo4j.graphdb.Transaction
import org.neo4j.kernel.EmbeddedGraphDatabase
import org.reudd.node.BaseNode
import org.reudd.node.DataNodeFactory
import org.reudd.node.DynamicRelationship
import org.reudd.node.TypeNodeFactory
import org.reudd.util.ReUddConstants



public class Reudd {

    public static void main(def args) {

        if (args.size() == 0 ) {
            doHelp()
        } else {
            switch (args[0]) {
                case "version":
                    doVersion()
                    break

                case "import":
                    doImport(args)
                    break

                case "help":
                default:
                    doHelp()
                    break
            }
        }
    }


    static def doHelp() {
        // todo schema output
        String result = "Usage: reudd <command> <options>\n" +
                "\n" +
                "   commands:\n" +
                "       import   or i    <importfile> <dbdir>\n" +
//                "       schema   or s\n" +
                "       help     or h\n" +
                "       version  or v"

        println( result )
        return result
    }


    static String doVersion() {
        //todo version number
        String result = "ReUDD v" + "//todo version";
        println(result )
        return result
    }


    static String getArg( def args, int i, String defaultValue ) {
        if (args.size() >= i+1 && !args[i].isEmpty()) {
            return args[1]
        } else {
            return defaultValue
        }
    }


    static def doImport(def args) {
        String importFiles = getArg(args, 1, "")
        String databasePath = getArg(args, 2, "data/neo4j")
        String result = ""

        if(!importFiles.isEmpty()) {

            importFiles.split(",").each { file ->
                println "Importing " + file + " into " + databasePath

                init()

                GraphDatabaseService graphDatabaseService = new EmbeddedGraphDatabase(databasePath, new HashMap<String, String>());
                def text = new FileInputStream(file)

                Transaction transaction
                transaction = graphDatabaseService.beginTx()

                result += file + " " + importFileSubmitSub(text, ";", graphDatabaseService) + "\n"

                transaction.success()
                transaction.finish()
                graphDatabaseService.shutdown()
            }

            return result
        } else {
            println("Error: Missing file to import.")
        }
    }


    def static init() {

        //todo This code duplicated. Used in Bootstrap. Resolve.

        /*
         *  Add getDynamicRelationship function to Node
         */
        Node.metaClass.getDynamicRelationships = {
            def dynamicRelationships = []
            delegate.getRelationships().each {
                if (!it.getType().name().startsWith(ReUddConstants.PREFIX)) {
                    dynamicRelationships += it
                }
            }
            dynamicRelationships
        }

        /*
         *  Add getDynamicRelationship function to Node
         */
        Node.metaClass.getDynamicRelationships = { Direction direction ->
            def dynamicRelationships = []
            delegate.getRelationships(direction).each {
                if (!it.getType().name().startsWith(ReUddConstants.PREFIX)) {
                    dynamicRelationships += it
                }
            }
            dynamicRelationships
        }

        /*
         *  Add getDynamicRelationship function to Node
         */
        Node.metaClass.getDynamicRelationships = { String name, Direction direction ->
            def dynamicRelationships = []
            delegate.getRelationships(direction).each {
                if (it.getType().name().equals(name)) {
                    dynamicRelationships += it
                }
            }
            dynamicRelationships
        }


        String.metaClass.isEmpty = {
            delegate == ""
        }


        String.metaClass.escapeSomeHtml = {
            //todo Check if we store html in database or not.
            //todo What about other characters outside of A-Z?
            delegate.replaceAll("å", "&aring;").replaceAll("Å", "&Aring;")
                    .replaceAll("ä", "&auml;").replaceAll("Ä", "&Auml;")
                    .replaceAll("ö", "&ouml;").replaceAll("Ö", "&Ouml;")
        }

    }


    def static importFileSubmitSub(importtext, delimiter, graphDatabaseService) {


        if (importtext) {
            def nodeList = []
            int nNodes = 0
            int nRelationships = 0

            nodeList = processImportText(importtext, delimiter)

//            println("Adding nodes")
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

                nNodes++
            }

//            println("Adding relationships")
            for (item in nodeList) {
                def thisNode = savedDataNodes[item]
                // Updates the incoming relationships to the node
                thisNode.inRelationships
                def relationList = []
                for (rel in item.relationships) {
                    def relDirectionOut = rel.startsWith(BaseNode.RELATIONSHIP_DIRECTION_OUT)
                    def relName = rel[3..rel.lastIndexOf("(") - 1]
                    def relTarget = rel[rel.lastIndexOf("(") + 1..-2]
                    def relTargetKey = relTarget[0..relTarget.lastIndexOf(":") - 1]
                    def relTargetValue = relTarget[relTarget.lastIndexOf(":") + 1..-1]
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

                nRelationships++
            }

            String result = "nodes:" + nNodes + ", relationships:" + + nRelationships
            println(result)
            return result

        }
    }


    static def processImportText(importText, delimiter) {
        def nodeList = []
        def headline = []
        def headLineImported = false

        importText.eachLine { line, index ->
            if (shouldImportLine(line)) {
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
                            } else if (title.startsWith(BaseNode.RELATIONSHIP_PREFIX)
                                    || title.startsWith(BaseNode.RELATIONSHIP_DIRECTION_IN_PREFIX)
                                    || title.startsWith(BaseNode.RELATIONSHIP_DIRECTION_OUT_PREFIX)) {
                                def relDirectionOut = !title.startsWith(BaseNode.RELATIONSHIP_DIRECTION_OUT_PREFIX)
                                def relDirection = relDirectionOut ? BaseNode.RELATIONSHIP_DIRECTION_OUT : BaseNode.RELATIONSHIP_DIRECTION_IN
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
        return !(isComment(line) || isEmptyLine(line))
    }

    private static boolean isEmptyLine(line) {
        return line.matches("[;]+")
    }

    private static boolean isComment(line) {
        return line.length() >= 2 && line.substring(0, 2) == "//"
    }

}