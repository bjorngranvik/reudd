package org.reudd.view.datamodel

import org.junit.Test
import org.reudd.node.TypeNode
import org.reudd.node.TypeNodeFactory

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*
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
    TypeNode firstNode = createTypeNodeMockWithNameAndOutgoingRelationships(nodeName, null, null)

    typeNodes = [firstNode]
    nodeFactory = createTypeNodeFactoryReturningTypeNodes(typeNodes)


    Map<String, NodeWithLinks> nodes = DataModelGenerator.getTypeModelFromTypeNodeFactory(nodeFactory)
    assertThat(nodes, is(instanceOf(LinkedHashMap.class)))
    assertThat(nodes.size(), is(typeNodes.size()))
    NodeWithLinks onlyNode = nodes.get(nodes.keySet().getAt(0))
    assertThat(onlyNode.name, is(nodeName))
    assertThat(onlyNode.index, is(0))
  }

  @Test
  public void getTwoTypesWithOneRelation() {
    def relationshipNames = ["relationship"]
    def nodeName1 = "node1"
    def nodeName2 = "node2"
    TypeNode firstNode = createTypeNodeMockWithNameAndOutgoingRelationships(nodeName1, relationshipNames, [nodeName2])
    TypeNode secondNode = createTypeNodeMockWithNameAndOutgoingRelationships(nodeName2, null, null)

    typeNodes = [firstNode, secondNode]
    nodeFactory = createTypeNodeFactoryReturningTypeNodes(typeNodes)

    Map<String, NodeWithLinks> nodes = DataModelGenerator.getTypeModelFromTypeNodeFactory(nodeFactory)
    assertThat(nodes.size(), is(typeNodes.size()))
    NodeWithLinks actual = nodes.get(nodeName1)
    assertThat(actual.links.size(), is(1))
    assertThat(actual.index, is(0))
    Link link = actual.links[0]
    assertThat(link.name, is(relationshipNames[0]))
    assertThat(link.source, is(nodeName1))
    assertThat(link.target, is(nodeName2))
    assertThat(actual.name, is(nodeName1))

    actual = nodes.get(nodeName2)
    assertThat(actual.index, is(1))
    assertThat(actual.name, is(nodeName2))
  }

  @Test
  public void getOneNodeWithOutgoingLinkToSelf() {
    String nodeName = "node"
    String relationshipName = "link"
    TypeNode typeNode = createTypeNodeMockWithNameAndOutgoingRelationships(nodeName, [relationshipName], [nodeName])

    typeNodes = [typeNode]
    nodeFactory = createTypeNodeFactoryReturningTypeNodes(typeNodes)

    Map<String, NodeWithLinks> nodes = DataModelGenerator.getTypeModelFromTypeNodeFactory(nodeFactory)
    assertThat(nodes.size(), is(1))
    NodeWithLinks actual = nodes.get(nodeName)
    assertThat(actual.name, is(nodeName))
    assertThat(actual.index, is(0))
    assertThat(actual.links.size(), is(1))
    Link link = actual.links[0]
    assertThat(link.name, is(relationshipName))
    assertThat(link.source, is(nodeName))
    assertThat(link.target, is(nodeName))
  }

  private
  static TypeNode createTypeNodeMockWithNameAndOutgoingRelationships(nodeName, relationshipNames, targetNames) {
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
    def actualDataModel = DataModelGenerator.transformTypeNodeModelToNodeAndLinkModelForD3representation(dataModelMap)
    assertThatDataModelMapContainsBothNodesAndLinks(actualDataModel)
    assertThat(actualDataModel["links"].size(), is(0))
    assertThat(actualDataModel["nodes"].size(), is(0))
  }

  @Test
  public void singleNodeWithNoLinks() {
    dataModelMap = ["Cocktail": new NodeWithLinks(name: COCKTAIL_NAME)]
    dataModelMap = ["Cocktail": new NodeWithLinks(name: COCKTAIL_NAME)]
    def actualDataModel = DataModelGenerator.transformTypeNodeModelToNodeAndLinkModelForD3representation(dataModelMap)
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
    dataModelMap = ["Cocktail": new NodeWithLinks(name: COCKTAIL_NAME, index: 0, links: [new Link(name: LINK_NAME, source: COCKTAIL_NAME, target: COCKTAIL_NAME)])]
    def actualDataMode = DataModelGenerator.transformTypeNodeModelToNodeAndLinkModelForD3representation(dataModelMap)
    assertThatDataModelMapContainsBothNodesAndLinks(actualDataMode)

    List<Node> nodes = actualDataMode["nodes"]
    assertThat(nodes.size(), is(1))
    Node node = nodes[0]
    assertThat(node.name, is(COCKTAIL_NAME))
    assertThat(node.index, is(0))
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
        "Cocktail": new NodeWithLinks(name: COCKTAIL_NAME, index: 0, links: [new Link(name: LINK_NAME, source: COCKTAIL_NAME, target: INGREDIENT_NAME)]),
        "Ingredient": new NodeWithLinks(name: INGREDIENT_NAME, index: 1)
    ]

    def actualDataModel = DataModelGenerator.transformTypeNodeModelToNodeAndLinkModelForD3representation(dataModelMap)
    assertThatDataModelMapContainsBothNodesAndLinks(actualDataModel)

    List<Node> nodes = actualDataModel["nodes"]
    assertThat(nodes.size(), is(2))
    List<Link> links = actualDataModel["links"]
    assertThat(links.size(), is(1))
    Node cocktail = nodes[0].name == COCKTAIL_NAME ? nodes[0] : nodes[1]
    Node ingredient = nodes[0].name == INGREDIENT_NAME ? nodes[0] : nodes[1]
    Link link = links[0]
    assertThat(link.source, is(cocktail.index))
    assertThat(link.target, is(ingredient.index))
    assertThat(link.name, is(LINK_NAME))

  }

  @Test
  public void fourNodesLinkedWithLinks() {
    String ingredientTypeName = "Ingredient Type"
    String glassName = "Glass"
    dataModelMap = [
        "Cocktail": new NodeWithLinks(name: COCKTAIL_NAME, index: 0, links: [
            new Link(name: "Contains", source: COCKTAIL_NAME, target: INGREDIENT_NAME),
            new Link(name: "Served in", source: COCKTAIL_NAME, target: glassName)
        ]),
        "Ingredient": new NodeWithLinks(name: INGREDIENT_NAME, index: 1, links: [
            new Link(name: "Type", source: INGREDIENT_NAME, target: ingredientTypeName),
            new Link(name: "Contains", source: INGREDIENT_NAME, target: COCKTAIL_NAME)
        ]),
        "Ingredient Type": new NodeWithLinks(name: ingredientTypeName, index: 2, links: [new Link(name: "Type", source: ingredientTypeName, target: INGREDIENT_NAME)]),
        "Glass": new NodeWithLinks(name: glassName, index: 3, links: [new Link(name: "Served in", source: glassName, target: COCKTAIL_NAME)])
    ]

    def actualDataModel = DataModelGenerator.transformTypeNodeModelToNodeAndLinkModelForD3representation(dataModelMap)
    assertThatDataModelMapContainsBothNodesAndLinks(actualDataModel)

    List<Node> nodes = actualDataModel["nodes"]
    assertThat(nodes.size(), is(4))
    List<Link> links = actualDataModel["links"]
    assertThat(links.size(), is(6))
    Node cocktail = nodes[0]
    assertThat(cocktail.name, is(COCKTAIL_NAME))
    assertThat(cocktail.index, is(0))
    Node ingredient = nodes[1]
    assertThat(ingredient.name, is(INGREDIENT_NAME))
    assertThat(ingredient.index, is(1))
    Node type = nodes[2]
    assertThat(type.name, is(ingredientTypeName))
    assertThat(type.index, is(2))
    Node glass = nodes[3]
    assertThat(glass.name, is(glassName))
    assertThat(glass.index, is(3))

    Link containsCocktail = links[0]
    assertThat(containsCocktail.name, is("Contains"))
    assertThat(containsCocktail.source, is(cocktail.index))
    assertThat(containsCocktail.target, is(ingredient.index))

    Link cocktailGlass = links[1]
    assertThat(cocktailGlass.name, is("Served in"))
    assertThat(cocktailGlass.source, is(cocktail.index))
    assertThat(cocktailGlass.target, is(glass.index))

    Link ingredientType = links[2]
    assertThat(ingredientType.name, is("Type"))
    assertThat(ingredientType.source, is(ingredient.index))
    assertThat(ingredientType.target, is(type.index))

    Link ingredientCocktail = links[3]
    assertThat(ingredientCocktail.name, is("Contains"))
    assertThat(ingredientCocktail.source, is(ingredient.index))
    assertThat(ingredientCocktail.target, is(cocktail.index))

    Link typeIngredient = links[4]
    assertThat(typeIngredient.name, is("Type"))
    assertThat(typeIngredient.source, is(type.index))
    assertThat(typeIngredient.target, is(ingredient.index))

    Link glassCocktail = links[5]
    assertThat(glassCocktail.name, is("Served in"))
    assertThat(glassCocktail.source, is(glass.index))
    assertThat(glassCocktail.target, is(cocktail.index))
  }
}