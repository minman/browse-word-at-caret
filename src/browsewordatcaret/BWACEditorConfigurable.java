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

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.UnnamedConfigurable;
import com.intellij.ui.IdeBorderFactory;

import javax.swing.*;

public class BWACEditorConfigurable implements UnnamedConfigurable {
    private JCheckBox cdAutoHighlight;
    private JCheckBox cdWrapAround;

    public BWACEditorConfigurable() {
        cdAutoHighlight = new JCheckBox("Highlight Word at Caret");
        cdWrapAround = new JCheckBox("Wrap around");
    }

    @Override
    public JComponent createComponent() {

        final JPanel bwacPanel = new JPanel();
        bwacPanel.setBorder(IdeBorderFactory.createTitledBorder("Browse Word At Caret", true));
        bwacPanel.setLayout(new BoxLayout(bwacPanel, BoxLayout.Y_AXIS));

        bwacPanel.add(cdAutoHighlight);
        bwacPanel.add(cdWrapAround);

        return bwacPanel;
    }

    @Override
    public boolean isModified() {
        BWACApplicationComponent component = BWACApplicationComponent.getInstance();
        return component.isAutoHighlight() != cdAutoHighlight.isSelected() ||
               component.isWrapAround() != cdWrapAround.isSelected() ;
    }

    @Override
    public void apply() throws ConfigurationException {
        BWACApplicationComponent component = BWACApplicationComponent.getInstance();
        component.setAutoHighlight(cdAutoHighlight.isSelected());
        component.setWrapAround(cdWrapAround.isSelected());
    }

    @Override
    public void reset() {
        BWACApplicationComponent component = BWACApplicationComponent.getInstance();
        cdAutoHighlight.setSelected(component.isAutoHighlight());
        cdWrapAround.setSelected(component.isWrapAround());
    }

    @Override
    public void disposeUIResources() {
    }
}
