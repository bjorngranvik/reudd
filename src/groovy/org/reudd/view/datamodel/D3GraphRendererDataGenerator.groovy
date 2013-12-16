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
import org.reudd.node.TypeNodeFactory

abstract class D3GraphRendererDataGenerator {

    private static final Map<String, List> EMPTY_DATA_MODEL = ["links": [], "nodes": []]

    private D3GraphRendererDataGenerator() {}

    static LinkedHashMap<String, NodeWithLinks> createDataModelDataUsingTypeNodeFactory(TypeNodeFactory nodeFactory) {
        def typeNodes = nodeFactory.getTypeNodes()
        LinkedHashMap<String, NodeWithLinks> results = [:]
        typeNodes.each { typeNode ->
            NodeWithLinks node = new NodeWithLinks(name: typeNode.name)
            typeNode.getOutgoingRelationshipNames().each { relationshipName ->
                typeNode.getOutgoingRelationshipTargetTypeNames(relationshipName).each { target ->
                    node.addLink(new Link(name: relationshipName, source: node.name, target: target))
                }
            }
            results.put(node.name, node)
        }
        results
    }

    static LinkedHashMap<String, NodeWithLinks> createNodeConnectionsUsingTypeNodeFactory(TypeNodeFactory nodeFactory) {
        def typeNodes = nodeFactory.getTypeNodes()
        LinkedHashMap<String, NodeWithLinks> results = [:]
        typeNodes.each { type ->
            def name = type.name
            NodeWithLinks n = new NodeWithLinks(name: name)

            typeNodes.each { otherType ->
                def percentage = type.getConnectionPercentagesToType(otherType.name)
                if (percentage != 0) {
                    n.addLink(new Link(name: percentage + "%", target: otherType.name))
                }
            }
            results.put(name, n)
        }
        results
    }

    static def transformTypeNodeModelToD3NodeAndLinkModel(
            final LinkedHashMap<String, NodeWithLinks> dataModelMap) {
        if (dataModelMap.size() == 0)
            return EMPTY_DATA_MODEL
        ArrayList<String> names = Lists.newArrayList(dataModelMap.keySet().iterator())
        List<Node> nodes = []
        List<Link> links = []
        dataModelMap.eachWithIndex { Map.Entry<String, NodeWithLinks> entry, int index ->
            def n = entry.value
            nodes.add(new Node(name: n.name, index: index))
            n.links.each { l ->
                links.add(new Link(name: l.name, source: index, target: names.indexOf(l.target)))
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
}

class Node {
    String name
    int index

    int getColourGroup() {
        return index
    }
}

class NodeWithLinks {
    String name
    List<Link> links = []

    void addLink(Link link) {
        links.add(link)
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

class Link {
    String name
    def source
    def target
}
