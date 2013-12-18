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
package org.reudd.view.datamodel

import com.google.common.collect.Lists
import org.neo4j.graphdb.Direction
import org.neo4j.graphdb.Relationship
import org.reudd.node.TypeNodeFactory
import org.reudd.statistics.NodePathBuilder
import org.reudd.util.ReUddConstants
import org.reudd.util.ReUddRelationshipTypes

abstract class D3GraphRendererDataGenerator {

    private static final Map<String, List> EMPTY_DATA_MODEL = ["links": [], "nodes": []]

    private D3GraphRendererDataGenerator() {}

    static LinkedHashMap<NodeId, NodeWithLinks> createDataModelDataUsingTypeNodeFactory(TypeNodeFactory nodeFactory) {
        def typeNodes = nodeFactory.getTypeNodes()
        LinkedHashMap<NodeId, NodeWithLinks> results = [:]
        typeNodes.each { typeNode ->
            NodeWithLinks node = new NodeWithLinks(typeNode.name)
            typeNode.getOutgoingRelationshipNames().each { relationshipName ->
                typeNode.getOutgoingRelationshipTargetTypeNames(relationshipName).each { targetName ->
                    node.addLink(relationshipName, new NodeId(targetName))
                }
            }
            results.put(node.id, node)
        }
        results
    }

    static LinkedHashMap<NodeId, NodeWithLinks> createNodeConnectionsUsingTypeNodeFactory(TypeNodeFactory nodeFactory) {
        def typeNodes = nodeFactory.getTypeNodes()
        LinkedHashMap<NodeId, NodeWithLinks> results = [:]
        typeNodes.each { type ->
            NodeWithLinks n = new NodeWithLinks(type.name, type.id)
            typeNodes.each { otherType ->
                def percentage = type.getConnectionPercentagesToType(otherType.id)
                NodeId otherTypeId = new NodeId(otherType.name, otherType.id)
                if (percentage != 0) {
                    n.addLink(percentage + "%", otherTypeId)
                }
            }
            results.put(n.id, n)
        }
        results
    }

    static def transformTypeNodeModelToD3NodeAndLinkModel(
            final LinkedHashMap<NodeId, NodeWithLinks> dataModelMap) {
        if (dataModelMap.size() == 0)
            return EMPTY_DATA_MODEL
        ArrayList<NodeId> ids = Lists.newArrayList(dataModelMap.keySet().iterator())
        List<Node> nodes = []
        List<Link> links = []
        dataModelMap.eachWithIndex { Map.Entry<String, NodeWithLinks> entry, int index ->
            def node = entry.value
            nodes.add(new Node(name: node.name, index: index))
            node.links.each { l ->
                links.add(new Link(l.name, index, ids.indexOf(l.target)))
            }
        }

        ["links": links, "nodes": nodes]
    }

    static def createNodeConnectionsForD3GraphRendering(TypeNodeFactory nodeFactory) {
        transformTypeNodeModelToD3NodeAndLinkModel(createNodeConnectionsUsingTypeNodeFactory(nodeFactory))
    }

    static def createD3DataModelFromTypeNodesFromTypeNodeFactory(TypeNodeFactory nodeFactory) {
        transformTypeNodeModelToD3NodeAndLinkModel(createDataModelDataUsingTypeNodeFactory(nodeFactory))
    }

    static def generateNavigatedPath(NodePathBuilder pathBuilder) {
        StringBuffer sb = new StringBuffer()

        org.neo4j.graphdb.Node rootNode = pathBuilder.statisticsNode
        Iterable<Relationship> relationships = rootNode.getRelationships(ReUddRelationshipTypes._REUDD_NODE_PATH, Direction.OUTGOING)
        int outCount = 0
        relationships.each { relationship ->
            Integer travCount = relationship.getProperty(ReUddConstants.NODE_PATH_TRAVERSED_COUNT)
            outCount += travCount
        }
        printRecursiveNavPaths(rootNode, sb, true, outCount)

        println "sb: " + sb.toString()
        sb.toString()
    }

    private static
    def printRecursiveNavPaths(org.neo4j.graphdb.Node node, StringBuffer returnBuffer, boolean isStartNode, int prevCount) {
        String nodeString = isStartNode ? "Start" : "No Type"
        if (node.hasProperty(ReUddConstants.STATISTIC_NODE_PATH_STRING)) {
            nodeString = node.getProperty(ReUddConstants.STATISTIC_NODE_PATH_STRING)
        }
        nodeString = nodeString.replaceAll(", ", "<br/>").escapeSomeHtml()
        println """$node.id: $nodeString"""
        returnBuffer.append """"$node.id" [label=<$nodeString>]"""
        def relationships = node.getRelationships(ReUddRelationshipTypes._REUDD_NODE_PATH, Direction.OUTGOING)
        relationships.each { relationship ->
            def endNode = relationship.endNode
            int travCount = relationship.getProperty(ReUddConstants.NODE_PATH_TRAVERSED_COUNT)
            def percentage = 100
            if (prevCount) {
                percentage = new BigDecimal((travCount / prevCount) * 100)
                percentage = percentage.setScale(0, BigDecimal.ROUND_HALF_UP)
            }
            returnBuffer.append """"$node.id" -> "$endNode.id" [label="  $percentage% ($travCount)  "]"""
            printRecursiveNavPaths(endNode, returnBuffer, false, travCount)
        }
    }
}


class Node {
    String name
    int index

    int getColourGroup() {
        return index
    }
}

class NodeWithLinks {
    final String name
    final NodeId id
    final List<Link> links = []

    NodeWithLinks(String name) {
        this.name = name
        this.id = new NodeId(name)
    }

    NodeWithLinks(String name, String id) {
        this.name = name
        this.id = new NodeId(name, id)
    }

    void addLink(String name, NodeId target) {
        links.add(new Link(name, this.id, target))
    }

    boolean equals(final o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        final NodeWithLinks node = (NodeWithLinks) o

        if (name != node.name) return false

        return true
    }

    int hashCode() {
        return name.hashCode()
    }
}

class NodeId {
    final String name
    final String id

    NodeId(String name) {
        this.name = name
        id = null
    }

    NodeId(String name, String id) {
        this.name = name
        this.id = id
    }

//    String getId() {
//        if (id == null)
//            return name
//        return id
//    }

    boolean equals(final o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        final NodeId nodeId = (NodeId) o
        if (id != nodeId.id) return false
        if (name != nodeId.name) return false

        return true
    }

    int hashCode() {
        int result
        result = name.hashCode()
        result = 31 * result + (id != null ? id.hashCode() : 0)
        return result
    }
}


class Link {
    final String name
    final def source
    final def target

    Link(String name, def source, def target) {
        this.name = name
        this.source = source
        this.target = target
    }
}
