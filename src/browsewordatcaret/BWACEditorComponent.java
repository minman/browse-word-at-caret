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
import com.intellij.openapi.editor.markup.HighlighterLayer;
import com.intellij.openapi.editor.markup.HighlighterTargetArea;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.util.TextRange;

import javax.swing.*;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.List;

public class BWACEditorComponent implements SelectionListener, CaretListener, DocumentListener {
    private Editor editor;
    private List<RangeHighlighter> rangeHighlighters;
    private String highlightText;
    private int updating;

    private static final int HIGHLIGHTLAYER = HighlighterLayer.SELECTION - 1; // unmittelbar unter Selektion-level

    public BWACEditorComponent(Editor editor) {
        this.editor = editor;

        rangeHighlighters = new ArrayList<RangeHighlighter>();

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
        if (rangeHighlighters.isEmpty()) {
            return;
        }
        for (RangeHighlighter rangeHighlighter : rangeHighlighters) {
            editor.getMarkupModel().removeHighlighter(rangeHighlighter);
        }
        rangeHighlighters.clear();
        highlightText = null;
    }

    public void buildHighlighters(String highlightText) {
        // textAttribute für RangeHighlighter holen
        TextAttributes textAttributes = editor.getColorsScheme().getAttributes(BWACColorSettingsPage.BROWSEWORDATCARET);

        // zuerst mal aktuelle löschen
        clearHighlighters();
        // text durchsuchen
        String text = editor.getDocument().getText();
        RangeHighlighter rangeHighlighter;
        int index = -1;
        do {
            index = text.indexOf(highlightText, index + 1);
            // wenn gefunden und ganzes wort ->
            if (index >= 0 && isFullWord(text, index, highlightText.length(), true)) {
                // RangeHighlighter erstellen
                rangeHighlighter = editor.getMarkupModel().addRangeHighlighter(index, index + highlightText.length(), HIGHLIGHTLAYER, textAttributes, HighlighterTargetArea.EXACT_RANGE);
                rangeHighlighter.setErrorStripeTooltip(highlightText);
                rangeHighlighters.add(rangeHighlighter);
            }
        } while (index >= 0);
        this.highlightText = highlightText;
    }

    public String getHighlightText() {
        return highlightText;
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
        if (!isFullWord(text, textRange.getStartOffset(), textRange.getLength(), false)) {
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
        if (!rangeHighlighters.isEmpty()) { // optimization
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    // wenn der Cursor ausserhalb eines unserer RangeHighlighter kommt -> clearen...
                    if (getRangeHighlighterIndex(editor.getCaretModel().getOffset()) < 0) {
                        clearHighlighters();
                    }
                }
            });
        }
    }

    @Override
    public void beforeDocumentChange(DocumentEvent documentEvent) {
    }

    @Override
    public void documentChanged(DocumentEvent documentEvent) {
        clearHighlighters();
    }

    public int getRangeHighlighterIndex(int offset) {
        if (!rangeHighlighters.isEmpty()) {
            for (int i = 0; i < rangeHighlighters.size(); i++) {
                RangeHighlighter rangeHighlighter = rangeHighlighters.get(i);
                if (offset >= rangeHighlighter.getStartOffset() && offset <= rangeHighlighter.getStartOffset() + highlightText.length()) {
                    return i;
                }
            }
        }
        return -1;
    }

    public void browse(BWACHandlerBrowse.BrowseDirection browseDirection) {
        updating++;
        try {
            // wenn noch keine RangeHighlights vorhanden ->
            if (rangeHighlighters.isEmpty()) {
                // aktuelles Wort unter dem Cursor nehmen...
                String text = editor.getDocument().getText();
                int currentOffset = editor.getCaretModel().getOffset();
                String currentWord = text.substring(getWordStartOffset(text, currentOffset), getWordEndOffset(text, currentOffset));

                if (currentWord.length() <= 0) {
                    return;
                }
                buildHighlighters(currentWord);
            }
            int index = getRangeHighlighterIndex(editor.getCaretModel().getOffset()) + (BWACHandlerBrowse.BrowseDirection.NEXT.equals(browseDirection) ? 1 : -1);

            if (index >= 0 && index < rangeHighlighters.size()) {
                int offset = rangeHighlighters.get(index).getStartOffset();
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
     * Repaint (-> RangeHighlighters neu erstellen)
     */
    public void repaint() {
        if (getHighlightText() != null && getHighlightText().length() > 0) {
            buildHighlighters(getHighlightText());
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
            for (int i = wordStartOffset; i < wordEndOffset; i++) {
                if (!isWordchar(text.charAt(i))) {
                    return false;
                }
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
}
