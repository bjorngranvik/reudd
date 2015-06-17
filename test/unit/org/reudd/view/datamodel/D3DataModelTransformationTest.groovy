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

package org.reudd.view.datamodel
import org.junit.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.instanceOf
import static org.hamcrest.Matchers.is
import static org.hamcrest.core.IsNull.notNullValue

class D3DataModelTransformationTest {
    private static final String COCKTAIL_NAME = "Cocktail"
    private static final String COCKTAIL_ID = "4"
    private static final String INGREDIENT_NAME = "Ingredient"
    private static final String INGREDIENT_ID = "8"
    private static final String LINK_NAME = "irrelevant name"
    private LinkedHashMap<NodeId, NodeWithLinks> dataModelMap = [:]


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
        NodeWithLinks startNode = new NodeWithLinks(COCKTAIL_NAME, COCKTAIL_ID)
        dataModelMap.put(startNode.id, startNode)
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
        NodeWithLinks startNode = new NodeWithLinks(COCKTAIL_NAME, COCKTAIL_ID)
        startNode.addLink(LINK_NAME, startNode.id)
        dataModelMap.put(startNode.id, startNode)
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
        NodeWithLinks cocktailNode = new NodeWithLinks(COCKTAIL_NAME, COCKTAIL_ID)
        NodeWithLinks ingredientNode = new NodeWithLinks(INGREDIENT_NAME, INGREDIENT_ID)
        cocktailNode.addLink(LINK_NAME, ingredientNode.id)
        dataModelMap.put(cocktailNode.id, cocktailNode)
        dataModelMap.put(ingredientNode.id, ingredientNode)

        def actualDataModel = D3GraphRendererDataGenerator.transformTypeNodeModelToD3NodeAndLinkModel(dataModelMap)
        assertThatDataModelMapContainsBothNodesAndLinks(actualDataModel)

        List<Node> nodes = actualDataModel["nodes"]
        assertThat(nodes.size(), is(2))
        List<Link> links = actualDataModel["links"]
        assertThat(links.size(), is(1))
        Link link = links[0]
        assertThat(link.source, is(0))
        assertThat(link.target, is(1))
        assertThat(link.name, is(LINK_NAME))

    }

    @Test
    public void fourLinkedNodes() {
        String ingredientTypeName = "Ingredient Type"
        String glassName = "Glass"
        NodeWithLinks cocktail = new NodeWithLinks(COCKTAIL_NAME, COCKTAIL_ID)
        NodeWithLinks ingredient = new NodeWithLinks(INGREDIENT_NAME)
        NodeWithLinks ingredientType = new NodeWithLinks(ingredientTypeName)
        NodeWithLinks glass = new NodeWithLinks(glassName)

        cocktail.addLink("Contains", ingredient.id)
        cocktail.addLink("Served in", glass.id)
        dataModelMap.put(cocktail.id, cocktail)

        ingredient.addLink("Type", ingredientType.id)
        ingredient.addLink("Contains", cocktail.id)
        dataModelMap.put(ingredient.id, ingredient)

        ingredientType.addLink("Type", ingredient.id)
        dataModelMap.put(ingredientType.id, ingredientType)

        glass.addLink("Served in", cocktail.id)
        dataModelMap.put(glass.id, glass)

        def actualDataModel = D3GraphRendererDataGenerator.transformTypeNodeModelToD3NodeAndLinkModel(dataModelMap)
        assertThatDataModelMapContainsBothNodesAndLinks(actualDataModel)

        List<Node> nodes = actualDataModel["nodes"]
        assertThat(nodes.size(), is(4))
        List<Link> links = actualDataModel["links"]
        assertThat(links.size(), is(6))
        Node actualCocktail = nodes[0]
        assertThat(actualCocktail.name, is(COCKTAIL_NAME))
        Node actualIngredient = nodes[1]
        assertThat(actualIngredient.name, is(INGREDIENT_NAME))
        Node actualType = nodes[2]
        assertThat(actualType.name, is(ingredientTypeName))
        Node actualGlass = nodes[3]
        assertThat(actualGlass.name, is(glassName))

        Link containsCocktail = links[0]
        assertThat(containsCocktail.name, is("Contains"))
        assertThat(containsCocktail.source, is(0))
        assertThat(containsCocktail.target, is(1))

        Link cocktailGlass = links[1]
        assertThat(cocktailGlass.name, is("Served in"))
        assertThat(cocktailGlass.source, is(0))
        assertThat(cocktailGlass.target, is(3))

        Link typeToIngredient = links[2]
        assertThat(typeToIngredient.name, is("Type"))
        assertThat(typeToIngredient.source, is(1))
        assertThat(typeToIngredient.target, is(2))

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
