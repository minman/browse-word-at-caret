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

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.actionSystem.EditorActionManager;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.event.EditorFactoryListener;
import com.intellij.openapi.editor.event.EditorFactoryEvent;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.*;
import org.jetbrains.annotations.NonNls;
import org.jdom.Element;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.awt.*;

public class BWACApplicationComponent implements ApplicationComponent, Configurable, EditorFactoryListener, JDOMExternalizable {
    private Map<Editor, BWACEditorComponent> editorComponents;
    private BWACConfigurationForm configurationForm;

    public static final boolean defaultPrefShowMarkup = true;
    public static final Color defaultPrefColorMarkup = new Color(82, 109, 165);
    public static final boolean defaultPrefShowHighlight = true;
    public static final Color defaultPrefColorHighlight = new Color(220, 226, 237);
    public static final boolean defaultPrefSelectWord = false;

    public boolean prefShowMarkup = defaultPrefShowMarkup;
    public String prefColorMarkup = ColorToString(defaultPrefColorMarkup);
    public boolean prefShowHighlight = defaultPrefShowHighlight;
    public String prefColorHighlight = ColorToString(defaultPrefColorHighlight);
    public boolean prefSelectWord = defaultPrefSelectWord;

    @NonNls
    public String getComponentName() {
        return getClass().getSimpleName();
    }

    public void initComponent() {
        editorComponents = new HashMap<Editor, BWACEditorComponent>();
        EditorFactory.getInstance().addEditorFactoryListener(this);

        // Copy
        final EditorActionHandler editorCopyHandler = EditorActionManager.getInstance().getActionHandler(IdeActions.ACTION_EDITOR_COPY);
        EditorActionManager.getInstance().setActionHandler(IdeActions.ACTION_EDITOR_COPY, new EditorActionHandler() {
            public void execute(Editor editor, DataContext dataContext) {
                selectWordUnderCaret(editor);
                editorCopyHandler.execute(editor, dataContext);
            }
        });
        // Cut
        final EditorActionHandler editorCutHandler = EditorActionManager.getInstance().getActionHandler(IdeActions.ACTION_EDITOR_CUT);
        EditorActionManager.getInstance().setActionHandler(IdeActions.ACTION_EDITOR_CUT, new EditorActionHandler() {
            public void execute(Editor editor, DataContext dataContext) {
                selectWordUnderCaret(editor);
                editorCutHandler.execute(editor, dataContext);
            }
        });
        // PasteReplace -> siehe BWACHandlerPasteReplace
    }

    public static void selectWordUnderCaret(Editor editor) {
        // wenn nichts selektiert ist -> Wort unter caret selektieren
        if (editor.getSelectionModel().getSelectedText() == null) {
            editor.getSelectionModel().selectWordAtCaret(false);
        }
    }

    public void disposeComponent() {
        EditorFactory.getInstance().removeEditorFactoryListener(this);
        for (BWACEditorComponent editorComponent : editorComponents.values()) {
            editorComponent.dispose();
        }
        editorComponents.clear();
    }

    public void editorCreated(EditorFactoryEvent editorFactoryEvent) {
        Editor editor = editorFactoryEvent.getEditor();
        if (editor.getProject() == null) {
            return;
        }
        BWACEditorComponent editorComponent = new BWACEditorComponent(editorFactoryEvent.getEditor());
        editorComponents.put(editorFactoryEvent.getEditor(), editorComponent);
    }

    public void editorReleased(EditorFactoryEvent editorFactoryEvent) {
        BWACEditorComponent editorComponent = editorComponents.remove(editorFactoryEvent.getEditor());
        if (editorComponent == null) {
            return;
        }
        editorComponent.dispose();
    }

    public BWACEditorComponent getEditorComponent(Editor editor) {
        return editorComponents.get(editor);
    }

    public String getDisplayName() {
        return "BrowseWordAtCaret";
    }

    public Icon getIcon() {
        return null;
    }

    public String getHelpTopic() {
        return null;
    }

    public boolean isModified() {
        if (configurationForm == null) {
            return false;
        }
        return configurationForm.isModified(this);
    }

    public void apply() throws ConfigurationException {
        if (configurationForm == null) {
            return;
        }
        configurationForm.getData(this);
        // repaint highlighters
        for (BWACEditorComponent editorComponent : editorComponents.values()) {
            editorComponent.repaint();
        }
    }

    public void reset() {
        if (configurationForm == null) {
            return;
        }
        configurationForm.setData(this);
    }

    public void disposeUIResources() {
        configurationForm = null;
    }

    public JComponent createComponent() {
        if (configurationForm == null) {
            configurationForm = new BWACConfigurationForm();
        }
        return (configurationForm.getMainPanel());
    }

    public static String ColorToString(Color color) {
        return String.valueOf(color.getRed()) + "," + String.valueOf(color.getGreen()) + "," + String.valueOf(color.getBlue());
    }

    public static Color StringToColor(String str) {
        String[] s = str.split(",");
        return s.length == 3 ? new Color(Integer.valueOf(s[0]), Integer.valueOf(s[1]), Integer.valueOf(s[2])) : null;
    }

    public void readExternal(Element element) throws InvalidDataException {
        DefaultJDOMExternalizer.readExternal(this, element);
    }

    public void writeExternal(Element element) throws WriteExternalException {
        DefaultJDOMExternalizer.writeExternal(this, element);
    }
}
