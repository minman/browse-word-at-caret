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

import com.intellij.codeInsight.hint.HintManager;
import com.intellij.codeInsight.hint.HintManagerImpl;
import com.intellij.codeInsight.hint.HintUtil;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.editor.event.*;
import com.intellij.openapi.editor.ex.MarkupModelEx;
import com.intellij.openapi.editor.markup.HighlighterLayer;
import com.intellij.openapi.editor.markup.HighlighterTargetArea;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.keymap.KeymapUtil;
import com.intellij.openapi.util.JDOMUtil;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.LightweightHint;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class BWACEditorComponent implements SelectionListener, CaretListener, DocumentListener {
    private Editor editor;
    private final List<RangeHighlighter> items = new ArrayList<RangeHighlighter>();
    private volatile int updating;
    private boolean autoHighlight;

    private static final int HIGHLIGHTLAYER = HighlighterLayer.SELECTION - 1; // unmittelbar unter Selektion-level
    private static final int UPDATEDELAY = 400;

    public BWACEditorComponent(Editor editor, boolean autoHighlight) {
        this.editor = editor;
        this.autoHighlight = autoHighlight;

        editor.getSelectionModel().addSelectionListener(this);
        editor.getCaretModel().addCaretListener(this);
        editor.getDocument().addDocumentListener(this);
    }

    public void dispose() {
//        clearHighlighters();
        editor.getSelectionModel().removeSelectionListener(this);
        editor.getCaretModel().removeCaretListener(this);
        editor.getDocument().removeDocumentListener(this);
//        editor = null;
    }

    @Override
    public void selectionChanged(SelectionEvent selectionEvent) {
        if (updating > 0) {
            return;
        }

        // wenn ColumnMode -> gibts nicht mehr zu tun
        if (editor.isColumnMode()) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    clearHighlighters(); // noch löschen, da vielleicht etwas selektiert war und umgestellt wurde
                }
            });
            return;
        }
        // Selektion wurde aufgehoben -> nichts machen -> sobald cursor ausserhalb kommt wird ge'cleared... ( siehe caretPositionChanged...)
        if (selectionEvent.getNewRange().getLength() == 0) {
            return;
        }

        String text = editor.getDocument().getText();
        TextRange textRange = selectionEvent.getNewRange();

        final boolean checkHumpBound = ServiceManager.getService(BWACApplicationService.class).getState().humpBound;

        // aufgrund selektiertem Text erstellen
        final String highlightText;
        if ((textRange.getStartOffset() != 0 || textRange.getEndOffset() != text.length()) && // fix issue 5: komplettem text ausschliessen
                BWACUtils.isStartEnd(text, textRange.getStartOffset(), textRange.getEndOffset(), false, checkHumpBound)) {
            highlightText = textRange.substring(text);
        } else {
            highlightText = null; // ansonsten löschen
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                buildHighlighters(highlightText, checkHumpBound);
            }
        });
    }

    private Timer caretChangedDelayTimer = new Timer(UPDATEDELAY, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            synchronized (items) {
                int currentOffset = editor.getCaretModel().getOffset();
                // wenn der Cursor innerhalb eines unserer RangeHighlighter kommt -> return
                if (!items.isEmpty() && getItemIndex(currentOffset) >= 0) {
                    return;
                }
                performHighlight();
            }
        }
    }) {
        {
            setRepeats(false);
        }
    };

    private void performHighlight() {
        // aktuelles Wort unter dem Cursor nehmen...
        final String wordToHighlight;
        if (autoHighlight && !editor.getSelectionModel().hasSelection()) {
            int currentOffset = editor.getCaretModel().getOffset();
            wordToHighlight = BWACUtils.extractWordFrom(editor.getDocument().getText(), currentOffset);
        } else {
            wordToHighlight = null;
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                buildHighlighters(wordToHighlight);
            }
        });
    }

    public boolean isAutoHighlight() {
        return autoHighlight;
    }

    public void setAutoHighlight(boolean autoHighlight) {
        if (this.autoHighlight != autoHighlight) {
            this.autoHighlight = autoHighlight;
            if (!editor.getSelectionModel().hasSelection()) {
                performHighlight();
            }
        }
    }

    @Override
    public void caretPositionChanged(CaretEvent caretEvent) {
        if (updating > 0) {
            return;
        }
        caretChangedDelayTimer.restart();
    }

    @Override
    public void beforeDocumentChange(DocumentEvent documentEvent) {
    }

    @Override
    public void documentChanged(DocumentEvent documentEvent) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                clearHighlighters(); // bei changed -> löschen
            }
        });
    }

    private static final Key<BWACHandlerBrowse.BrowseDirection> KEY = Key.create("BWACHandlerBrowse.BrowseDirection.KEY");

    public void browse(@NotNull final BWACHandlerBrowse.BrowseDirection browseDirection) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                updating++;
                try {
                    synchronized (items) {
                        // wenn noch keine RangeHighlights vorhanden ->
                        if (items.isEmpty() || caretChangedDelayTimer.isRunning()) {
                            // aktuelles Wort unter dem Cursor nehmen...
                            String currentWord = BWACUtils.extractWordFrom(editor.getDocument().getText(), editor.getCaretModel().getOffset());
                            if (currentWord == null) {
                                return; // kein wort -> nichts zu machen
                            }
                            buildHighlighters(currentWord);
                        }
                        int index = getItemIndex(editor.getCaretModel().getOffset()) + (BWACHandlerBrowse.BrowseDirection.NEXT.equals(browseDirection) ? 1 : -1);

                        if (index >= 0 && index < items.size()) {
                            int offset = items.get(index).getStartOffset();
                            moveToOffset(offset);
                        } else if (items.size() > 0) {
                            if (editor.getUserData(KEY) == browseDirection) {
                                // performed again -> start from top/bottom
                                editor.putUserData(KEY, null);
                                int offset = items.get(browseDirection == BWACHandlerBrowse.BrowseDirection.NEXT ? 0 : items.size() - 1).getStartOffset();
                                moveToOffset(offset);
                            } else {
                                final String message;
                                if (ServiceManager.getService(BWACApplicationService.class).getState().wrapAround) {
                                    // top/bottom reached -> remember browse direction
                                    editor.putUserData(KEY, browseDirection);
                                    CaretListener listener = new CaretListener() {
                                        @Override
                                        public void caretPositionChanged(CaretEvent e) {
                                            editor.putUserData(KEY, null);
                                            editor.getCaretModel().removeCaretListener(this);
                                        }
                                    };
                                    editor.getCaretModel().addCaretListener(listener);
                                    message = getPerformAgainMessage(browseDirection);
                                } else {
                                    message = "Not found";
                                }
                                // show hint
                                LightweightHint hint = new LightweightHint(HintUtil.createInformationLabel(message));
                                HintManagerImpl.getInstanceImpl().showEditorHint(hint, editor,
                                        browseDirection == BWACHandlerBrowse.BrowseDirection.NEXT ? HintManager.UNDER : HintManager.ABOVE,
                                        HintManager.HIDE_BY_ANY_KEY | HintManager.HIDE_BY_TEXT_CHANGE | HintManager.HIDE_BY_SCROLLING,
                                        0, false);
                            }
                        }
                    }
                } finally {
                    updating--;
                }
            }
        });
    }

    @NotNull
    private String getPerformAgainMessage(@NotNull final BWACHandlerBrowse.BrowseDirection browseDirection) {
        AnAction action = ActionManager.getInstance().getAction(browseDirection == BWACHandlerBrowse.BrowseDirection.NEXT ? "BrowseWordAtCaretPlugin.Next" : "BrowseWordAtCaretPlugin.Previous");
        String shortcutsText = KeymapUtil.getFirstKeyboardShortcutText(action);
        String message;
        if (shortcutsText.length() > 0) {
            if (browseDirection == BWACHandlerBrowse.BrowseDirection.NEXT) {
                message = "Not found, press " + shortcutsText + " to browse from the top";
            } else {
                message = "Not found, press " + shortcutsText + " to browse from the bottom";
            }
        } else {
            if (browseDirection == BWACHandlerBrowse.BrowseDirection.NEXT) {
                message = "Not found, perform \"Browse to next word\" again to browse from the top";
            } else {
                message = "Not found, perform \"Browse to previous word\" again to browse from the bottom";
            }
        }
        return JDOMUtil.escapeText(message, false, false);
    }

    private int getItemIndex(int offset) {
        synchronized (items) {
            for (int i = 0; i < items.size(); i++) {
                RangeHighlighter item = items.get(i);
                if (offset >= item.getStartOffset() && offset <= item.getEndOffset()) {
                    return i;
                }
            }
        }
        return -1;
    }

    // DISPATCH THREAD METHODS

    private void moveToOffset(int offset) {
        ApplicationManager.getApplication().assertIsDispatchThread();
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

    private void clearHighlighters() {
        buildHighlighters(null, false);
    }

    private void buildHighlighters(final String highlightText) {
        buildHighlighters(highlightText, false);
    }

    private void buildHighlighters(final String highlightText, boolean checkHumpBound) {
        ApplicationManager.getApplication().assertIsDispatchThread();
        synchronized (items) {
            // aktuelle löschen
            final MarkupModelEx markupModel = (MarkupModelEx) editor.getMarkupModel();
            for (RangeHighlighter rangeHighlighter : items) {
                if (markupModel.containsHighlighter(rangeHighlighter)) {
                    markupModel.removeHighlighter(rangeHighlighter);
                }
            }
            items.clear();
            // und erstellen
            if (!StringUtil.isEmpty(highlightText)) {
                // text durchsuchen
                String text = editor.getDocument().getText();
                // textAttribute für RangeHighlighter holen
                final TextAttributes textAttributes = editor.getColorsScheme().getAttributes(BWACColorSettingsPage.BROWSEWORDATCARET);

                int index = -1;
                do {
                    index = text.indexOf(highlightText, index + 1);
                    // wenn gefunden und ganzes wort -> aufnehmen
                    if (index >= 0 && BWACUtils.isStartEnd(text, index, index + highlightText.length(), true, checkHumpBound)) {
                        RangeHighlighter rangeHighlighter = markupModel.addRangeHighlighter(index, index + highlightText.length(), HIGHLIGHTLAYER, textAttributes, HighlighterTargetArea.EXACT_RANGE);
                        rangeHighlighter.setErrorStripeTooltip(highlightText);
                        items.add(rangeHighlighter);
                    }
                } while (index >= 0);
            }
        }
    }
}
