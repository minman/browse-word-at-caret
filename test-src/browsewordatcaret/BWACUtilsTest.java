/*
 * Copyright 2013 Minas Manthos
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package browsewordatcaret;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test von {@link BWACUtils}.
 */
public class BWACUtilsTest {
    @Test
    public void testExtractWordFrom() throws Exception {
        assertNull(BWACUtils.extractWordFrom("", 0));

        assertNull(BWACUtils.extractWordFrom("foo  bar baz", -1));

        assertEquals("foo", BWACUtils.extractWordFrom("foo  bar baz", 0));
        assertEquals("foo", BWACUtils.extractWordFrom("foo  bar baz", 1));
        assertEquals("foo", BWACUtils.extractWordFrom("foo  bar baz", 2));
        assertEquals("foo", BWACUtils.extractWordFrom("foo  bar baz", 3));

        assertNull(BWACUtils.extractWordFrom("foo  bar baz", 4));

        assertEquals("bar", BWACUtils.extractWordFrom("foo  bar baz", 5));
        assertEquals("bar", BWACUtils.extractWordFrom("foo  bar baz", 6));
        assertEquals("bar", BWACUtils.extractWordFrom("foo  bar baz", 7));
        assertEquals("bar", BWACUtils.extractWordFrom("foo  bar baz", 8));

        assertEquals("baz", BWACUtils.extractWordFrom("foo  bar baz", 9));
        assertEquals("baz", BWACUtils.extractWordFrom("foo  bar baz", 10));
        assertEquals("baz", BWACUtils.extractWordFrom("foo  bar baz", 11));
        assertEquals("baz", BWACUtils.extractWordFrom("foo  bar baz", 12));

        assertNull(BWACUtils.extractWordFrom("foo  bar baz", 13));

        assertNull(BWACUtils.extractWordFrom("foobar", 2)); // fix issue 5: komplettem text ausschliessen
    }

    @Test
    public void testIsStartEnd() throws Exception {
        assertEquals(false, BWACUtils.isStartEnd("foo  bar baz", 0, 2, false, false));
        assertEquals(true,  BWACUtils.isStartEnd("foo  bar baz", 0, 3, false, false));
        assertEquals(false, BWACUtils.isStartEnd("foo  bar baz", 0, 4, false, false));
        assertEquals(false, BWACUtils.isStartEnd("foo  bar baz", -1, 2, false, false));
        assertEquals(false, BWACUtils.isStartEnd("foo  bar baz", -1, 3, false, false));
        assertEquals(false, BWACUtils.isStartEnd("foo  bar baz", -1, 4, false, false));
        assertEquals(false, BWACUtils.isStartEnd("foo  bar baz", 1, 2, false, false));
        assertEquals(false, BWACUtils.isStartEnd("foo  bar baz", 1, 3, false, false));
        assertEquals(false, BWACUtils.isStartEnd("foo  bar baz", 1, 4, false, false));

        assertEquals(false, BWACUtils.isStartEnd("foo  bar baz", 5, 7, false, false));
        assertEquals(true,  BWACUtils.isStartEnd("foo  bar baz", 5, 8, false, false));
        assertEquals(false, BWACUtils.isStartEnd("foo  bar baz", 5, 9, false, false));
        assertEquals(false, BWACUtils.isStartEnd("foo  bar baz", 4, 7, false, false));
        assertEquals(false, BWACUtils.isStartEnd("foo  bar baz", 4, 8, false, false));
        assertEquals(false, BWACUtils.isStartEnd("foo  bar baz", 4, 9, false, false));
        assertEquals(false, BWACUtils.isStartEnd("foo  bar baz", 6, 7, false, false));
        assertEquals(false, BWACUtils.isStartEnd("foo  bar baz", 6, 8, false, false));
        assertEquals(false, BWACUtils.isStartEnd("foo  bar baz", 6, 9, false, false));

        assertEquals(false, BWACUtils.isStartEnd("foo  bar baz", 9, 11, false, false));
        assertEquals(true,  BWACUtils.isStartEnd("foo  bar baz", 9, 12, false, false));
        assertEquals(false, BWACUtils.isStartEnd("foo  bar baz", 9, 13, false, false));
        assertEquals(false, BWACUtils.isStartEnd("foo  bar baz", 8, 11, false, false));
        assertEquals(false, BWACUtils.isStartEnd("foo  bar baz", 8, 12, false, false));
        assertEquals(false, BWACUtils.isStartEnd("foo  bar baz", 8, 13, false, false));
        assertEquals(false, BWACUtils.isStartEnd("foo  bar baz", 10, 11, false, false));
        assertEquals(false, BWACUtils.isStartEnd("foo  bar baz", 10, 12, false, false));
        assertEquals(false, BWACUtils.isStartEnd("foo  bar baz", 10, 13, false, false));
    }
}
