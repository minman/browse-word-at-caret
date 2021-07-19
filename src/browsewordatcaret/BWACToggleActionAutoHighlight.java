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

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.Editor;
import com.sun.istack.internal.Nullable;

public class BWACToggleActionAutoHighlight extends ToggleAction {
    @Override
    public void update(AnActionEvent e) {
        if (getEditor(e) == null) {
            e.getPresentation().setEnabled(false);
            e.getPresentation().setVisible(false);
        } else {
            e.getPresentation().setEnabled(true);
            e.getPresentation().setVisible(true);
            super.update(e);
        }
    }

    @Override
    public boolean isSelected(AnActionEvent e) {
        BWACEditorComponent editorComponent = getEditorComponent(e);
        return editorComponent != null && editorComponent.isAutoHighlight();
    }

    @Override
    public void setSelected(AnActionEvent e, boolean state) {
        BWACEditorComponent editorComponent = getEditorComponent(e);
        if (editorComponent != null) {
            editorComponent.setAutoHighlight(state);
        }
    }

    @Nullable
    private static Editor getEditor(AnActionEvent e) {
        return e.getData(PlatformDataKeys.EDITOR);
    }

    @Nullable
    private static BWACEditorComponent getEditorComponent(AnActionEvent e) {
        return ServiceManager.getService(BWACApplicationService.class).getEditorComponent(getEditor(e));
    }
}
