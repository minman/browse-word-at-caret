/*
 * Copyright 2021 Minas Manthos
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
import com.intellij.openapi.editor.event.EditorFactoryEvent;
import com.intellij.openapi.editor.event.EditorFactoryListener;
import org.jetbrains.annotations.NotNull;

public class BWACEditorFactoryListener implements EditorFactoryListener {

    @Override
    public void editorCreated(@NotNull EditorFactoryEvent event) {
        Editor editor = event.getEditor();
        if (editor.getProject() == null) {
            return;
        }
        BWACEditorComponent editorComponent = new BWACEditorComponent(editor, BWACApplicationService.getService().getState().autoHighlight);
        editor.putUserData(BWACEditorComponent.EDITOR_COMPONENT_KEY, editorComponent);
    }

    @Override
    public void editorReleased(@NotNull EditorFactoryEvent event) {
        Editor editor = event.getEditor();
        BWACEditorComponent userData = editor.getUserData(BWACEditorComponent.EDITOR_COMPONENT_KEY);
        if (userData != null) {
            editor.putUserData(BWACEditorComponent.EDITOR_COMPONENT_KEY, null);
            userData.dispose();
        }
    }
}
