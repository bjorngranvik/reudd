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


import grails.test.ControllerUnitTestCase
import org.junit.Test

class UserControllerTest extends ControllerUnitTestCase {

    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    @Test
    void testProcessImportText() {

        def text = new FileInputStream('test/unit/UserControllerTestImportText.txt')
        def result = controller.processImportText(text, ";")
        println(result)

        assertEquals("Should have four rows of input.", 4, result.size)
        assertEquals("Should be two types for first row.", 2, result.types.get(0).size)
        assertEquals("Should be one type for second row.", 1, result.types.get(1).size)

        assertEquals("Björn", result.attributes.get(0).name)
        assertEquals("50", result.attributes.get(0).age)
    }
}
