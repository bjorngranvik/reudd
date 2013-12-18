package org.reudd.view.datamodel
import org.junit.Test
import org.reudd.node.TypeNode
import org.reudd.node.TypeNodeFactory

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.instanceOf
import static org.hamcrest.Matchers.is
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

class GenerateDataModelMapTest {

    private List<TypeNode> typeNodes
    private TypeNodeFactory nodeFactory


    @Test
    public void getSingleTypeWhenThereAreNoRelations() {
        def nodeName = "firstNode"
        TypeNode firstNode = mockNode(nodeName, null, null)

        typeNodes = [firstNode]
        nodeFactory = createTypeNodeFactoryReturningTypeNodes(typeNodes)


        Map<String, NodeWithLinks> nodes = D3GraphRendererDataGenerator.createDataModelDataUsingTypeNodeFactory(nodeFactory)
        assertThat(nodes, is(instanceOf(LinkedHashMap.class)))
        assertThat(nodes.size(), is(typeNodes.size()))
        NodeWithLinks onlyNode = nodes.get(nodes.keySet().getAt(0))
        assertThat(onlyNode.name, is(nodeName))
    }

    @Test
    public void getTwoTypesWithOneRelation() {
        def relationshipNames = ["relationship"]
        def nodeName1 = "node1"
        def nodeName2 = "node2"
        TypeNode firstNode = mockNode(nodeName1, relationshipNames, [nodeName2])
        TypeNode secondNode = mockNode(nodeName2, null, null)

        typeNodes = [firstNode, secondNode]
        nodeFactory = createTypeNodeFactoryReturningTypeNodes(typeNodes)

        Map<NodeId, NodeWithLinks> nodes = D3GraphRendererDataGenerator.createDataModelDataUsingTypeNodeFactory(nodeFactory)
        assertThat(nodes.size(), is(typeNodes.size()))
        NodeId expectedNodeId1 = new NodeId(nodeName1)
        NodeId expectedNodeId2 = new NodeId(nodeName2)
        NodeWithLinks actual = nodes.get(expectedNodeId1)
        assertThat(actual.links.size(), is(1))
        Link link = actual.links[0]
        assertThat(link.name, is(relationshipNames[0]))
        assertThat(link.source, is(expectedNodeId1))
        assertThat(link.target, is(expectedNodeId2))
        assertThat(actual.name, is(nodeName1))

        actual = nodes.get(expectedNodeId2)
        assertThat(actual.name, is(nodeName2))
    }

    @Test
    public void getOneNodeWithOutgoingLinkToSelf() {
        String nodeName = "node"
        String relationshipName = "link"
        TypeNode typeNode = mockNode(nodeName, [relationshipName], [nodeName])

        typeNodes = [typeNode]
        nodeFactory = createTypeNodeFactoryReturningTypeNodes(typeNodes)

        Map<NodeId, NodeWithLinks> nodes = D3GraphRendererDataGenerator.createDataModelDataUsingTypeNodeFactory(nodeFactory)
        assertThat(nodes.size(), is(1))
        NodeId expectedNodeId = new NodeId(nodeName)
        NodeWithLinks actual = nodes.get(expectedNodeId)
        assertThat(actual.name, is(nodeName))
        assertThat(actual.links.size(), is(1))
        Link link = actual.links[0]
        assertThat(link.name, is(relationshipName))
        assertThat(link.source, is(expectedNodeId))
        assertThat(link.target, is(expectedNodeId))
    }

    private
    static TypeNode mockNode(nodeName, relationshipNames, targetNames) {
        [
                getName: { nodeName },
                getOutgoingRelationshipNames: { relationshipNames },
                getOutgoingRelationshipTargetTypeNames: { targetNames }

        ] as TypeNode
    }

    private static TypeNodeFactory createTypeNodeFactoryReturningTypeNodes(typeNodes) {
        [getTypeNodes: { typeNodes }] as TypeNodeFactory
    }
}

