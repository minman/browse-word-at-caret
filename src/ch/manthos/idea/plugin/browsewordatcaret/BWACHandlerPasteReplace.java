/*
 * Copyright (c) 2005 Minas Manthos, mailto:minas@manthos.ch
 */
package ch.manthos.idea.plugin.browsewordatcaret;

import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.actionSystem.EditorActionManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.IdeActions;

public class BWACHandlerPasteReplace extends EditorActionHandler {
    public void execute(Editor editor, DataContext dataContext) {
        BWACApplicationComponent.selectWordUnderCaret(editor);

        String selectedText = editor.getSelectionModel().getSelectedText();

        if (selectedText != null && selectedText.length() > 0) {
            EditorActionManager.getInstance().getActionHandler(IdeActions.ACTION_EDITOR_DELETE).execute(editor, dataContext);
            EditorActionManager.getInstance().getActionHandler(IdeActions.ACTION_EDITOR_PASTE).execute(editor, dataContext);
        }
    }
}
