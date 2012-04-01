/*
 * Copyright 2012 Minas Manthos
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

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileTypes.PlainSyntaxHighlighter;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class BWACColorSettingsPage implements ColorSettingsPage {
    private static final TextAttributes DEFAULT_BROWSEWORDATCARET = new TextAttributes(null, new Color(220, 226, 237), null, null, Font.PLAIN);
    static {
        DEFAULT_BROWSEWORDATCARET.setErrorStripeColor(new Color(82, 109, 165));
    }

    public static final TextAttributesKey BROWSEWORDATCARET = TextAttributesKey.createTextAttributesKey("BROWSEWORDATCARET", DEFAULT_BROWSEWORDATCARET);

    private static final AttributesDescriptor[] ATTRIBUTESDESC = {new AttributesDescriptor("BrowseWordAtCaret", BROWSEWORDATCARET)};

    private static final Map<String, TextAttributesKey> TAGS = new HashMap<String, TextAttributesKey>();
    static {
        TAGS.put("browseWordAtCaret", BROWSEWORDATCARET);
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "BrowseWordAtCaret";
    }

    @Override
    public Icon getIcon() {
        return null;
    }

    @NotNull
    @Override
    public AttributesDescriptor[] getAttributeDescriptors() {
        return ATTRIBUTESDESC;
    }

    @NotNull
    @Override
    public ColorDescriptor[] getColorDescriptors() {
        return ColorDescriptor.EMPTY_ARRAY;
    }

    @NotNull
    @Override
    public SyntaxHighlighter getHighlighter() {
        return new PlainSyntaxHighlighter();
    }

    @NotNull
    @Override
    public String getDemoText() {
        return "Allows to easily <browseWordAtCaret>browse</browseWordAtCaret> next/previous word at caret.";
    }

    @Override
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        return TAGS;
    }
}
