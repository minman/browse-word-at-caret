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

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.editor.event.*;
import com.intellij.openapi.editor.markup.*;
import com.intellij.openapi.util.TextRange;

import javax.swing.*;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.List;

public class BWACEditorComponent implements SelectionListener, CaretListener, DocumentListener {
    private Editor editor;
    private List<Item> items = new ArrayList<Item>();
    private int highlightLength;
    private int updating;

    private static final int HIGHLIGHTLAYER = HighlighterLayer.SELECTION - 1; // unmittelbar unter Selektion-level

    public BWACEditorComponent(Editor editor) {
        this.editor = editor;

        editor.getSelectionModel().addSelectionListener(this);
        editor.getCaretModel().addCaretListener(this);
        editor.getDocument().addDocumentListener(this);
    }

    public void dispose() {
        clearHighlighters();
        editor.getSelectionModel().removeSelectionListener(this);
        editor.getCaretModel().removeCaretListener(this);
        editor.getDocument().removeDocumentListener(this);
        editor = null;
    }

    private void clearHighlighters() {
        if (items.isEmpty()) {
            return;
        }
        final List<Item> currentItems = new ArrayList<Item>(items);
        items.clear();
        final MarkupModel markupModel = editor.getMarkupModel();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                for (Item item : currentItems) {
                    if (item.rangeHighlighter != null) {
                        markupModel.removeHighlighter(item.rangeHighlighter);
                    }
                }
            }
        });
    }

    private void buildHighlighters(final String highlightText) {
        // zuerst mal aktuelle löschen
        clearHighlighters();
        // text durchsuchen
        String text = editor.getDocument().getText();
        int index = -1;
        do {
            index = text.indexOf(highlightText, index + 1);
            // wenn gefunden und ganzes wort -> aufnehmen
            if (index >= 0 && isFullWord(text, index, highlightText.length(), true)) {
                items.add(new Item(index));
            }
        } while (index >= 0);
        this.highlightLength = highlightText.length();

        // textAttribute für RangeHighlighter holen
        final TextAttributes textAttributes = editor.getColorsScheme().getAttributes(BWACColorSettingsPage.BROWSEWORDATCARET);

        final MarkupModel markupModel = editor.getMarkupModel();
        final List<Item> currentItems = new ArrayList<Item>(items);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                for (Item item : currentItems) {
                    item.rangeHighlighter = markupModel.addRangeHighlighter(item.startOffset, item.startOffset + highlightText.length(), HIGHLIGHTLAYER, textAttributes, HighlighterTargetArea.EXACT_RANGE);
                    item.rangeHighlighter.setErrorStripeTooltip(highlightText);
                }
            }
        });
    }

    @Override
    public void selectionChanged(SelectionEvent selectionEvent) {
        if (updating > 0) {
            return;
        }

        // wenn ColumnMode -> gibts nicht mehr zu tun
        if (editor.isColumnMode()) {
            clearHighlighters(); // noch löschen, da vielleicht etwas selektiert war und umgestellt wurde
            return;
        }
        // Selektion wurde aufgehoben -> nichts machen -> sobald cursor ausserhalb kommt wird ge'cleared... ( siehe caretPositionChanged...)
        if (selectionEvent.getNewRange().getLength() == 0) {
            return;
        }

        String text = editor.getDocument().getText();
        TextRange textRange = selectionEvent.getNewRange();
        if ((textRange.getStartOffset() == 0 && textRange.getLength() == text.length()) || // fix issue 5: bei komplettem text -> kein highlight
                !isFullWord(text, textRange.getStartOffset(), textRange.getLength(), false)) {
            clearHighlighters();
            return;
        }
        // aufgrund selektiertem Text erstellen
        buildHighlighters(text.substring(textRange.getStartOffset(), textRange.getEndOffset()));
    }

    @Override
    public void caretPositionChanged(CaretEvent caretEvent) {
        if (updating > 0) {
            return;
        }
        // wenn der Cursor ausserhalb eines unserer RangeHighlighter kommt -> clearen...
        if (getItemIndex(editor.getCaretModel().getOffset()) < 0) {
            clearHighlighters();
        }
    }

    @Override
    public void beforeDocumentChange(DocumentEvent documentEvent) {
    }

    @Override
    public void documentChanged(DocumentEvent documentEvent) {
        clearHighlighters();
    }

    private int getItemIndex(int offset) {
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            if (offset >= item.startOffset && offset <= item.startOffset + highlightLength) {
                return i;
            }
        }
        return -1;
    }

    public void browse(BWACHandlerBrowse.BrowseDirection browseDirection) {
        updating++;
        try {
            // wenn noch keine RangeHighlights vorhanden ->
            if (items.isEmpty()) {
                // aktuelles Wort unter dem Cursor nehmen...
                String text = editor.getDocument().getText();
                int currentOffset = editor.getCaretModel().getOffset();
                String currentWord = text.substring(getWordStartOffset(text, currentOffset), getWordEndOffset(text, currentOffset));

                if (currentWord.length() <= 0) {
                    return;
                }
                buildHighlighters(currentWord);
            }
            int index = getItemIndex(editor.getCaretModel().getOffset()) + (BWACHandlerBrowse.BrowseDirection.NEXT.equals(browseDirection) ? 1 : -1);

            if (index >= 0 && index < items.size()) {
                int offset = items.get(index).startOffset;
                // Cursor setzen
                editor.getCaretModel().moveToOffset(offset);
                /*
                // Wort selektieren
                if (ApplicationManager.getApplication().getComponent(BWACApplicationComponent.class).prefSelectWord) {
                    editor.getSelectionModel().setSelection(offset, offset + highlightText.length());
                }
                */
                // in sichtbaren Bereich bringen
                editor.getScrollingModel().scrollToCaret(ScrollType.MAKE_VISIBLE);
            }
        } finally {
            updating--;
        }
    }

    /**
     * Wortanfang suchen.
     *
     * @param text
     * @param currentOffset
     * @return offset des Wortanfang
     */
    private static int getWordStartOffset(final String text, final int currentOffset) {
        StringCharacterIterator sci = new StringCharacterIterator(text, currentOffset);
        while (isWordchar(sci.previous())) {/*nothing to do here...*/}
        return sci.getIndex()+1;
    }

    /**
     * Wortende suchen.
     *
     * @param text
     * @param currentOffset
     * @return offset des Wortende
     */
    private static int getWordEndOffset(final String text, final int currentOffset) {
        StringCharacterIterator sci = new StringCharacterIterator(text, (currentOffset > 0 ? currentOffset - 1 : 0));
        while (isWordchar(sci.next())) {/*nothing to do here...*/}
        return sci.getIndex();
    }

    /**
     * Testen ob Wort an offset ein volles wort ist (so soll zB wenn 'Value' gesucht wird, dieses im Text 'getValue' nicht gefunden werden...
     *
     * @param text
     * @param wordStartOffset
     * @param length
     * @param checkOnlyPrePostCharacter
     * @return <tt>true<tt>, wenn volles Wort...
     */
    private static boolean isFullWord(final String text, final int wordStartOffset, final int length, final boolean checkOnlyPrePostCharacter) {
        // test pre-char
        if (wordStartOffset > 0) {
            if (isWordchar(text.charAt(wordStartOffset - 1)))
                return false; // vorhergehendes Zeichen ist Wortzeichen -> ist nicht volles Wort...
        }

        final int wordEndOffset = wordStartOffset + length;

        // test the range
        if (!checkOnlyPrePostCharacter) {
            /* issue 2/3 statt alle zeichen...
            for (int i = wordStartOffset; i < wordEndOffset; i++) {
                if (!isWordchar(text.charAt(i))) {
                    return false;
                }
            }
            */
            // erstes und letztes zeichen prüfen ob isWordchar
            if (!isWordchar(text.charAt(wordStartOffset)) || !isWordchar(text.charAt(wordEndOffset - 1))) {
                return false;
            }
        }

        // test post-char
        if (wordEndOffset < text.length()) {
            if (isWordchar(text.charAt(wordEndOffset)))
                return false; // nachfolgendes Zeichen ist Wortzeichen -> ist nicht volles Wort...
        }

        return true;
    }

    /**
     * Prüfen, ob das Zeichen ein Buchstabe (und kein Trennzeichen) ist (und somit resp. dadurch ein Wort beendet ist)
     *
     * @param currentChar zu prüfendes Zeichen
     * @return <tt>true</tt>, wenn es sich um einen Buchstaben handelt
     */
    private static boolean isWordchar(char currentChar) {
        return (( currentChar >=  65 ) && (currentChar <=  90)) || // A..Z
               (( currentChar >=  97 ) && (currentChar <= 122)) || // a..z
               (( currentChar ==  95 )                        ) || // _
               (( currentChar >=  48 ) && (currentChar <=  57)) || // 0..9
               (( currentChar >= 192 ) && (currentChar <= 255) && (currentChar != 215) && (currentChar != 247))  // À..ÿ (ohne ×, ÷)
            ;
    }

    private static class Item {
        private int startOffset;
        private RangeHighlighter rangeHighlighter;

        private Item(int startOffset) {
            this.startOffset = startOffset;
        }
    }
}
