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

    public BWACEditorConfigurable() {
        cdAutoHighlight = new JCheckBox("Highlight Word at Caret");
    }

    @Override
    public JComponent createComponent() {

        final JPanel bwacPanel = new JPanel();
        bwacPanel.setBorder(IdeBorderFactory.createTitledBorder("Browse Word At Caret", true));
        bwacPanel.setLayout(new BoxLayout(bwacPanel, BoxLayout.Y_AXIS));

        bwacPanel.add(cdAutoHighlight);

        return bwacPanel;
    }

    @Override
    public boolean isModified() {
        return BWACApplicationComponent.getInstance().isAutoHighlight() != cdAutoHighlight.isSelected();
    }

    @Override
    public void apply() throws ConfigurationException {
        BWACApplicationComponent.getInstance().setAutoHighlight(cdAutoHighlight.isSelected());
    }

    @Override
    public void reset() {
        cdAutoHighlight.setSelected(BWACApplicationComponent.getInstance().isAutoHighlight());
    }

    @Override
    public void disposeUIResources() {
    }
}
