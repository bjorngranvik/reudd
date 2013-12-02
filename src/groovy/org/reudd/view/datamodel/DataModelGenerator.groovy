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

import org.reudd.node.TypeNodeFactory

abstract class DataModelGenerator {

  private static final Map<String, List> EMPTY_DATA_MODEL = ["links": [], "nodes": []]

  private DataModelGenerator() {}

  static LinkedHashMap<String, NodeWithLinks> getTypeModelFromTypeNodeFactory(TypeNodeFactory nodeFactory) {
    def typeNodes = nodeFactory.getTypeNodes()
    LinkedHashMap<String, NodeWithLinks> nodes = [:]
    int index = 0
    typeNodes.each { typeNode ->
      NodeWithLinks node = new NodeWithLinks(name: typeNode.name, index: index)
      typeNode.getOutgoingRelationshipNames().each { relationshipName ->
        typeNode.getOutgoingRelationshipTargetTypeNames(relationshipName).each { target ->
          node.addLink(new Link(name: relationshipName, source: node.name, target: target))
        }
      }
      nodes.put(node.name, node)
      index++
    }
    nodes
  }

  static def transformTypeNodeModelToNodeAndLinkModelForD3representation(final LinkedHashMap<String, NodeWithLinks> dataModelMap) {
    if (dataModelMap.size() == 0)
      return EMPTY_DATA_MODEL
    List<Node> nodes = []
    List<Link> links = []
    dataModelMap.eachWithIndex { Map.Entry<String, NodeWithLinks> entry, int index ->
      def n = entry.value
      nodes.add(new Node(name: n.name, index: index))
      n.links.each {l ->
        links.add(new Link(name: l.name, source: index, target: dataModelMap[l.target].index))
      }
    }

    ["links": links, "nodes": nodes]
  }

  static def createD3DataModelFromTypeNodesFromTypeNodeFactory(TypeNodeFactory nodeFactory) {
    transformTypeNodeModelToNodeAndLinkModelForD3representation(getTypeModelFromTypeNodeFactory(nodeFactory))
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
  int index
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
