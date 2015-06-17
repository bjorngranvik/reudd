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
import static org.hamcrest.Matchers.is
import static org.hamcrest.Matchers.not

class NodeIdEqualityTest {

    @Test
    public void nodeIdsWithoutID() {
        NodeId first = new NodeId("first")
        NodeId sameAsFirst = new NodeId("first")
        NodeId second = new NodeId("second")
        assertThat(first, is(sameAsFirst))
        assertThat(first, is(not(second)))
    }

    @Test
    public void nodeWithIdIsNotSameAsNodeWithoutId() {
        NodeId withId = new NodeId("name", "id")
        NodeId sameAsWithoutId = new NodeId("name")
        assertThat(withId, is(not(sameAsWithoutId)))
    }

    @Test
    public void nodeWithDifferentNameAndIdAreDifferent() {
        NodeId first = new NodeId("first", "firstId")
        NodeId sameAsSecond = new NodeId("second", "secondId")
        assertThat(first, is(not(sameAsSecond)))
    }

    @Test
    public void nodeWithSameIdAndDifferentNameAreEqual() {
        NodeId firstWithId = new NodeId("first", "id")
        NodeId sameAsSecondWithId = new NodeId("second", "id")
        assertThat(firstWithId, is(sameAsSecondWithId))
    }

    @Test
    public void nodesWithSameNameAndDifferentIdsAreNotEqual() {
        NodeId first = new NodeId("name", "id1")
        NodeId sameAsFirst = new NodeId("name", "id2")
        assertThat(first, is(not(sameAsFirst)))
    }

    @Test
    public void nodeWithNameAndIdIsSameAsNodeWithOnlyId() {
        NodeId first = new NodeId("name", 1)
        NodeId sameAsFirst = new NodeId(1)
        assertThat(first, is(sameAsFirst))
    }

}
