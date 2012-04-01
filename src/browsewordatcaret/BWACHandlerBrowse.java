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
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;

public class BWACHandlerBrowse extends EditorActionHandler {
    private BrowseDirection browseDirection;

    public BWACHandlerBrowse(BrowseDirection browseDirection) {
        this.browseDirection = browseDirection;
    }

    public void execute(Editor editor, DataContext dataContext) {
        BWACApplicationComponent applicationComponent = ApplicationManager.getApplication().getComponent(BWACApplicationComponent.class);
        BWACEditorComponent editorComponent = applicationComponent.getEditorComponent(editor);
        if (editorComponent != null) {
            editorComponent.browse(browseDirection);
        }
    }

    public static enum BrowseDirection {
        NEXT, PREVIOUS
    }
}
