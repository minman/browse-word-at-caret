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

import com.intellij.openapi.editor.actions.EditorActionUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class BWACUtils {
    private BWACUtils() {
    }

    /**
     * Liefert das Wort aus dem Text gem. index
     */
    @Nullable
    public static String extractWordFrom(@NotNull String text, int index) {
        int length = text.length();
        if (length <= 0 || index < 0 || index > length) {
            return null;
        }
        int begin = index;
        while (begin > 0 && Character.isJavaIdentifierPart(text.charAt(begin - 1))) {
            begin--;
        }
        int end = index;
        while (end < length && Character.isJavaIdentifierPart(text.charAt(end))) {
            end++;
        }
        if (end <= begin || (begin == 0 && end == length) ) {
            return null; // fix issue 5: komplettem text ausschliessen
        }
        return text.substring(begin, end);
    }

    /**
     * Prüft, ob bei begin/end ein Wort beginnt und endet.
     */
    public static boolean isStartEnd(@NotNull final String text, final int begin, final int end, boolean checkOnlyPreviousNext, boolean checkHumpBound) {
        int length = text.length();
        if (length <= 0 || begin < 0 || begin >= length || length <= 0 || end <= 0 || end > length) {
            return false;
        }
        // previous char
        if (begin != 0 && Character.isJavaIdentifierPart(text.charAt(begin - 1))) {
            if (!checkHumpBound || !EditorActionUtil.isHumpBound(text, begin, true)) {
                return false;
            }
        }
        // next char
        if (end != length && Character.isJavaIdentifierPart(text.charAt(end))) {
            if (!checkHumpBound || !EditorActionUtil.isHumpBound(text, end, false)) {
                return false;
            }
        }
        // first/last char from text
        if (!checkOnlyPreviousNext) {
            if (!Character.isJavaIdentifierPart(text.charAt(begin)) || !Character.isJavaIdentifierPart(text.charAt(end - 1))) {
                return false;
            }
        }
        return true;
    }
}
