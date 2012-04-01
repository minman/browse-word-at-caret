/*
 * Copyright (c) 2005 Minas Manthos, mailto:minas@manthos.ch
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
