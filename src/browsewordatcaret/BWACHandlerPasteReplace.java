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

import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.actionSystem.EditorActionManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.IdeActions;

public class BWACHandlerPasteReplace extends EditorActionHandler {
    @Override
    public void execute(Editor editor, DataContext dataContext) {
        BWACApplicationComponent.selectWordUnderCaret(editor);

        String selectedText = editor.getSelectionModel().getSelectedText();

        if (selectedText != null && selectedText.length() > 0) {
            EditorActionManager.getInstance().getActionHandler(IdeActions.ACTION_EDITOR_DELETE).execute(editor, dataContext);
            EditorActionManager.getInstance().getActionHandler(IdeActions.ACTION_EDITOR_PASTE).execute(editor, dataContext);
        }
    }
}
