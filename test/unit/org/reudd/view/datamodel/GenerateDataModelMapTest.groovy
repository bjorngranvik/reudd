package org.reudd.view.datamodel

import org.junit.Test
import org.reudd.node.TypeNode
import org.reudd.node.TypeNodeFactory

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.instanceOf
import static org.hamcrest.Matchers.is
import static org.hamcrest.core.IsNull.notNullValue

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

        Map<String, NodeWithLinks> nodes = D3GraphRendererDataGenerator.createDataModelDataUsingTypeNodeFactory(nodeFactory)
        assertThat(nodes.size(), is(typeNodes.size()))
        NodeWithLinks actual = nodes.get(nodeName1)
        assertThat(actual.links.size(), is(1))
        Link link = actual.links[0]
        assertThat(link.name, is(relationshipNames[0]))
        assertThat(link.source, is(nodeName1))
        assertThat(link.target, is(nodeName2))
        assertThat(actual.name, is(nodeName1))

        actual = nodes.get(nodeName2)
        assertThat(actual.name, is(nodeName2))
    }

    @Test
    public void getOneNodeWithOutgoingLinkToSelf() {
        String nodeName = "node"
        String relationshipName = "link"
        TypeNode typeNode = mockNode(nodeName, [relationshipName], [nodeName])

        typeNodes = [typeNode]
        nodeFactory = createTypeNodeFactoryReturningTypeNodes(typeNodes)

        Map<String, NodeWithLinks> nodes = D3GraphRendererDataGenerator.createDataModelDataUsingTypeNodeFactory(nodeFactory)
        assertThat(nodes.size(), is(1))
        NodeWithLinks actual = nodes.get(nodeName)
        assertThat(actual.name, is(nodeName))
        assertThat(actual.links.size(), is(1))
        Link link = actual.links[0]
        assertThat(link.name, is(relationshipName))
        assertThat(link.source, is(nodeName))
        assertThat(link.target, is(nodeName))
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

class DataModelArrayGenerationTest {
    private static final String COCKTAIL_NAME = "Cocktail"
    private static final String INGREDIENT_NAME = "Ingredient"
    private static final String LINK_NAME = "irrelevant name"
    private LinkedHashMap<String, NodeWithLinks> dataModelMap


    private static void assertThatDataModelMapContainsBothNodesAndLinks(actualDataModel) {
        assertThat(actualDataModel, is(notNullValue()))
        assertThat(actualDataModel["links"], is(instanceOf(List.class)))
        assertThat(actualDataModel["nodes"], is(instanceOf(List.class)))
    }

    @Test
    public void noNodesAndNoLinks() {
        dataModelMap = [:]
        def actualDataModel = D3GraphRendererDataGenerator.transformTypeNodeModelToD3NodeAndLinkModel(dataModelMap)
        assertThatDataModelMapContainsBothNodesAndLinks(actualDataModel)
        assertThat(actualDataModel["links"].size(), is(0))
        assertThat(actualDataModel["nodes"].size(), is(0))
    }

    @Test
    public void singleNodeWithNoLinks() {
        dataModelMap = ["Cocktail": new NodeWithLinks(name: COCKTAIL_NAME)]
        dataModelMap = ["Cocktail": new NodeWithLinks(name: COCKTAIL_NAME)]
        def actualDataModel = D3GraphRendererDataGenerator.transformTypeNodeModelToD3NodeAndLinkModel(dataModelMap)
        assertThatDataModelMapContainsBothNodesAndLinks(actualDataModel)
        assertThat(actualDataModel["links"].size(), is(0))
        List<Node> nodes = actualDataModel["nodes"]
        assertThat(nodes.size(), is(1))
        Node node = nodes[0]
        assertThat(node.name, is(COCKTAIL_NAME))
        assertThat(node, is(instanceOf(Node.class)))
        assertThat(node.index, is(0))
    }

    @Test
    public void singleNodeWithLinkToSelf() {
        dataModelMap = ["Cocktail": new NodeWithLinks(name: COCKTAIL_NAME, links: [new Link(name: LINK_NAME, source: COCKTAIL_NAME, target: COCKTAIL_NAME)])]
        def actualDataMode = D3GraphRendererDataGenerator.transformTypeNodeModelToD3NodeAndLinkModel(dataModelMap)
        assertThatDataModelMapContainsBothNodesAndLinks(actualDataMode)

        List<Node> nodes = actualDataMode["nodes"]
        assertThat(nodes.size(), is(1))
        Node node = nodes[0]
        assertThat(node.name, is(COCKTAIL_NAME))
        assertThat(node.colourGroup, is(0))

        List<Link> links = actualDataMode["links"]
        assertThat(links.size(), is(1))
        Link link = links[0]
        assertThat(link.name, is(LINK_NAME))
        assertThat(link.source, is(0))
        assertThat(link.target, is(0))
    }

    @Test
    public void twoNodesWithLinkFromOneToOther() {
        dataModelMap = [
                "Cocktail": new NodeWithLinks(name: COCKTAIL_NAME, links: [new Link(name: LINK_NAME, source: COCKTAIL_NAME, target: INGREDIENT_NAME)]),
                "Ingredient": new NodeWithLinks(name: INGREDIENT_NAME)
        ]

        def actualDataModel = D3GraphRendererDataGenerator.transformTypeNodeModelToD3NodeAndLinkModel(dataModelMap)
        assertThatDataModelMapContainsBothNodesAndLinks(actualDataModel)

        List<Node> nodes = actualDataModel["nodes"]
        assertThat(nodes.size(), is(2))
        List<Link> links = actualDataModel["links"]
        assertThat(links.size(), is(1))
        Node cocktail = nodes[0]
        Node ingredient = nodes[1]
        Link link = links[0]
        assertThat(link.source, is(0))
        assertThat(link.target, is(1))
        assertThat(link.name, is(LINK_NAME))

    }

    @Test
    public void fourNodesLinkedWithLinks() {
        String ingredientTypeName = "Ingredient Type"
        String glassName = "Glass"
        dataModelMap = [
                "Cocktail": new NodeWithLinks(name: COCKTAIL_NAME, links: [
                        new Link(name: "Contains", source: COCKTAIL_NAME, target: INGREDIENT_NAME),
                        new Link(name: "Served in", source: COCKTAIL_NAME, target: glassName)
                ]),
                "Ingredient": new NodeWithLinks(name: INGREDIENT_NAME, links: [
                        new Link(name: "Type", source: INGREDIENT_NAME, target: ingredientTypeName),
                        new Link(name: "Contains", source: INGREDIENT_NAME, target: COCKTAIL_NAME)
                ]),
                "Ingredient Type": new NodeWithLinks(name: ingredientTypeName, links: [new Link(name: "Type", source: ingredientTypeName, target: INGREDIENT_NAME)]),
                "Glass": new NodeWithLinks(name: glassName, links: [new Link(name: "Served in", source: glassName, target: COCKTAIL_NAME)])
        ]

        def actualDataModel = D3GraphRendererDataGenerator.transformTypeNodeModelToD3NodeAndLinkModel(dataModelMap)
        assertThatDataModelMapContainsBothNodesAndLinks(actualDataModel)

        List<Node> nodes = actualDataModel["nodes"]
        assertThat(nodes.size(), is(4))
        List<Link> links = actualDataModel["links"]
        assertThat(links.size(), is(6))
        Node cocktail = nodes[0]
        assertThat(cocktail.name, is(COCKTAIL_NAME))
        Node ingredient = nodes[1]
        assertThat(ingredient.name, is(INGREDIENT_NAME))
        Node type = nodes[2]
        assertThat(type.name, is(ingredientTypeName))
        Node glass = nodes[3]
        assertThat(glass.name, is(glassName))

        Link containsCocktail = links[0]
        assertThat(containsCocktail.name, is("Contains"))
        assertThat(containsCocktail.source, is(0))
        assertThat(containsCocktail.target, is(1))

        Link cocktailGlass = links[1]
        assertThat(cocktailGlass.name, is("Served in"))
        assertThat(cocktailGlass.source, is(0))
        assertThat(cocktailGlass.target, is(3))

        Link ingredientType = links[2]
        assertThat(ingredientType.name, is("Type"))
        assertThat(ingredientType.source, is(1))
        assertThat(ingredientType.target, is(2))

        Link ingredientCocktail = links[3]
        assertThat(ingredientCocktail.name, is("Contains"))
        assertThat(ingredientCocktail.source, is(1))
        assertThat(ingredientCocktail.target, is(0))

        Link typeIngredient = links[4]
        assertThat(typeIngredient.name, is("Type"))
        assertThat(typeIngredient.source, is(2))
        assertThat(typeIngredient.target, is(1))

        Link glassCocktail = links[5]
        assertThat(glassCocktail.name, is("Served in"))
        assertThat(glassCocktail.source, is(3))
        assertThat(glassCocktail.target, is(0))
    }
}
