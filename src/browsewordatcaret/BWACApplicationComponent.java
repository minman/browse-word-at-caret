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

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.EditorFactoryEvent;
import com.intellij.openapi.editor.event.EditorFactoryListener;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@State(name = "BWACSettings", storages = {@Storage(id = "other", file = "$APP_CONFIG$/options.xml")})
public class BWACApplicationComponent implements ApplicationComponent, EditorFactoryListener, PersistentStateComponent<BWACApplicationComponent.BWACSettings> {
    private Map<Editor, BWACEditorComponent> editorComponents;
    private BWACSettings settings = new BWACSettings();

    @NotNull
    public static BWACApplicationComponent getInstance() {
        return ApplicationManager.getApplication().getComponent(BWACApplicationComponent.class);
    }

    @NonNls
    @Override
    @NotNull
    public String getComponentName() {
        return "BWACApplicationComponent";
    }

    @Override
    public void initComponent() {
        editorComponents = new HashMap<Editor, BWACEditorComponent>();
        EditorFactory.getInstance().addEditorFactoryListener(this);
    }

    @Override
    public void disposeComponent() {
        EditorFactory.getInstance().removeEditorFactoryListener(this);
        for (BWACEditorComponent editorComponent : editorComponents.values()) {
            editorComponent.dispose();
        }
        editorComponents.clear();
    }

    @Override
    public void editorCreated(EditorFactoryEvent editorFactoryEvent) {
        Editor editor = editorFactoryEvent.getEditor();
        if (editor.getProject() == null) {
            return;
        }
        BWACEditorComponent editorComponent = new BWACEditorComponent(editor, settings.autoHighlight);
        editorComponents.put(editor, editorComponent);
    }

    @Override
    public void editorReleased(EditorFactoryEvent editorFactoryEvent) {
        BWACEditorComponent editorComponent = editorComponents.remove(editorFactoryEvent.getEditor());
        if (editorComponent == null) {
            return;
        }
        editorComponent.dispose();
    }

    @Nullable
    public BWACEditorComponent getEditorComponent(@Nullable Editor editor) {
        return editorComponents.get(editor);
    }

    public boolean isAutoHighlight() {
        return settings.autoHighlight;
    }

    public void setAutoHighlight(boolean autoHighlight) {
        settings.autoHighlight = autoHighlight;
        for (BWACEditorComponent editorComponent : editorComponents.values()) {
            editorComponent.setAutoHighlight(autoHighlight);
        }
    }

    @Override
    public BWACSettings getState() {
        return settings;
    }

    @Override
    public void loadState(BWACSettings state) {
        this.settings = state;
    }

    public static class BWACSettings {
        public boolean autoHighlight;
    }
}
