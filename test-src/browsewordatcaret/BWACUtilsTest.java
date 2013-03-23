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
        assertFalse(BWACUtils.isWordStart("foo  bar", -1, false));
        assertTrue(BWACUtils.isWordStart("foo  bar", 0, false));
        assertFalse(BWACUtils.isWordStart("foo  bar", 1, false));
        assertFalse(BWACUtils.isWordStart("foo  bar", 2, false));
        assertFalse(BWACUtils.isWordStart("foo  bar", 3, false));
        assertFalse(BWACUtils.isWordStart("foo  bar", 4, false));
        assertTrue(BWACUtils.isWordStart("foo  bar", 5, false));
        assertFalse(BWACUtils.isWordStart("foo  bar", 6, false));
        assertFalse(BWACUtils.isWordStart("foo  bar", 7, false));
        assertFalse(BWACUtils.isWordStart("foo  bar", 8, false));
        assertFalse(BWACUtils.isWordStart("foo  bar", 9, false));

        assertFalse(BWACUtils.isWordEnd("foo  bar", -1, false));
        assertFalse(BWACUtils.isWordEnd("foo  bar", 0, false));
        assertFalse(BWACUtils.isWordEnd("foo  bar", 1, false));
        assertFalse(BWACUtils.isWordEnd("foo  bar", 2, false));
        assertTrue(BWACUtils.isWordEnd("foo  bar", 3, false));
        assertFalse(BWACUtils.isWordEnd("foo  bar", 4, false));
        assertFalse(BWACUtils.isWordEnd("foo  bar", 5, false));
        assertFalse(BWACUtils.isWordEnd("foo  bar", 6, false));
        assertFalse(BWACUtils.isWordEnd("foo  bar", 7, false));
        assertTrue(BWACUtils.isWordEnd("foo  bar", 8, false));
        assertFalse(BWACUtils.isWordEnd("foo  bar", 9, false));
    }
}
