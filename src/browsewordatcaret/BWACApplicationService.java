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

import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.*;
import com.intellij.openapi.editor.Editor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@Service
@State(name = "BWACSettings", storages = {@Storage(value = "$APP_CONFIG$/options.xml", roamingType = RoamingType.DISABLED)})
public final class BWACApplicationService implements PersistentStateComponent<BWACApplicationService.BWACSettings>, Disposable {
    private Map<Editor, BWACEditorComponent> editorComponents = new HashMap<>();
    private BWACSettings settings = new BWACSettings();

    @Nullable
    public BWACEditorComponent getEditorComponent(@Nullable Editor editor) {
        return editorComponents.get(editor);
    }

    @Override
    public void dispose() {
        for (BWACEditorComponent editorComponent : editorComponents.values()) {
            editorComponent.dispose();
        }
        editorComponents.clear();
    }

    void editorCreated(@NotNull Editor editor) {
        if (editor.getProject() == null) {
            return;
        }
        BWACEditorComponent editorComponent = new BWACEditorComponent(editor, settings.autoHighlight);
        editorComponents.put(editor, editorComponent);
    }

    void editorReleased(@NotNull Editor editor) {
        BWACEditorComponent editorComponent = editorComponents.remove(editor);
        if (editorComponent == null) {
            return;
        }
        editorComponent.dispose();
    }

    @NotNull
    @Override
    public BWACSettings getState() {
        return settings;
    }

    @Override
    public void loadState(@NotNull BWACSettings state) {
        this.settings = state;
    }

    public static class BWACSettings {
        public boolean autoHighlight;
        public boolean wrapAround;
        public boolean humpBound;
    }
}
