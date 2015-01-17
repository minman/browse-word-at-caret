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
        while (begin > 0 && isWordchar(text.charAt(begin - 1))) {
            begin--;
        }
        int end = index;
        while (end < length && isWordchar(text.charAt(end))) {
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
    public static boolean isStartEnd(@NotNull final String text, final int begin, final int end, boolean checkOnlyPreviousNext) {
        return isWordStart(text, begin, checkOnlyPreviousNext) && isWordEnd(text, end, checkOnlyPreviousNext);
    }

    static boolean isWordStart(@NotNull final String text, final int begin, boolean checkOnlyPrevious) {
        int length = text.length();
        return (length > 0 && begin >= 0 && begin < length) &&
                (begin == 0 || !isWordchar(text.charAt(begin - 1))) &&
                (checkOnlyPrevious || isWordchar(text.charAt(begin)));
    }

    static boolean isWordEnd(@NotNull final String text, final int end, boolean checkOnlyNext) {
        int length = text.length();
        return (length > 0 && end > 0 && end <= length) &&
                (end == length || !isWordchar(text.charAt(end))) &&
                (checkOnlyNext || isWordchar(text.charAt(end - 1)));
    }

    /**
     * Prüfen, ob das Zeichen ein Buchstabe (und kein Trennzeichen) ist (und somit resp. dadurch ein Wort beendet ist)
     *
     * @param currentChar zu prüfendes Zeichen
     * @return <tt>true</tt>, wenn es sich um einen Buchstaben handelt
     */
    private static boolean isWordchar(char currentChar) {
        return Character.isJavaIdentifierPart(currentChar);
    }
}
