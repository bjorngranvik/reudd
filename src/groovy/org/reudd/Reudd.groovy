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
                case "v":
                    doVersion()
                    break

                case "import":
                case "i":
                    doImport(args)
                    break

                case "schema":
                case "s":
                    doSchema(args)
                    break

                case "help":
                case "h":
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
                "       import   or i    <importfile> <separator> <dbdir>\n" +
                "       schema   or s    <outputfilename> <fileformat> <dbdir>\n" +
                "       help     or h\n" +
                "       version  or v"

        println( result )
        return result
    }


    static String doVersion() {
        def config = new ConfigSlurper("dev").parse(new File("ReuddConfig.groovy").toURI().toURL())
        String version = config.app.version

        String result = "ReUDD v" + version;
        println(result )
        return result
    }


    static String getArg( def args, int i, String defaultValue ) {
        if (args.size() >= i+1 && !args[i].isEmpty()) {
            return args[i]
        } else {
            return defaultValue
        }
    }


    static def doImport(def args) {
        String importFiles = getArg(args, 1, "")
        String separator = getArg(args, 2, "\t")
        String databasePath = getArg(args, 3, "data/neo4j")
        String result = ""

        if(!importFiles.isEmpty()) {

            importFiles.split(",").each { file ->
                println "Importing " + file + " into " + databasePath

                init()

                GraphDatabaseService graphDatabaseService = new EmbeddedGraphDatabase(databasePath, new HashMap<String, String>());
                def text = new FileInputStream(file)

                Transaction transaction
                transaction = graphDatabaseService.beginTx()

                result += file + " " + importFileSubmitSub(text, separator, graphDatabaseService) + "\n"

                transaction.success()
                transaction.finish()
                graphDatabaseService.shutdown()
            }

            return result
        } else {
            println("Error: Missing file to import.")
        }
    }


    static def doSchema (def args) {
        String outputfilename = getArg(args, 1, "reudd_schema")
        String fileformat = getArg(args, 2, "svg")
        String databasePath = getArg(args, 3, "data/neo4j")
        println "Output to " + outputfilename + "." + fileformat + " from " + databasePath

        if(!outputfilename.isEmpty()) {
            init()

            GraphDatabaseService graphDatabaseService = new EmbeddedGraphDatabase(databasePath, new HashMap<String, String>());
            def dotBuffer = new StringWriter()
            def out = new PrintWriter(dotBuffer)

            Transaction transaction
            transaction = graphDatabaseService.beginTx()

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

            transaction.success()
            transaction.finish()
            graphDatabaseService.shutdown()

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
            Process p = runtime.exec("dot -T" + fileformat)
            //Process p = runtime.exec("/usr/local/bin/dot -Tpng")
            p.outputStream.withStream { stream ->
                stream << dotBuffer.toString()
            }

            def imageBuffer = new ByteArrayOutputStream()
            imageBuffer << p.inputStream
            byte[] image = imageBuffer.toByteArray()

            File to = new File(outputfilename + "." + fileformat);
            FileOutputStream fos = new FileOutputStream(to);
            fos.write(image);
            fos.close();
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