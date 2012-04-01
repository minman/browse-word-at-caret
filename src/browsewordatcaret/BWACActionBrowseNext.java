/*
 * Copyright (c) 2005 Minas Manthos, mailto:minas@manthos.ch
 */
package browsewordatcaret;

import com.intellij.openapi.editor.actionSystem.EditorAction;

public final class BWACActionBrowseNext extends EditorAction {
    public BWACActionBrowseNext() {
        super(new BWACHandlerBrowse(BWACHandlerBrowse.BrowseDirection.NEXT));
    }
}

