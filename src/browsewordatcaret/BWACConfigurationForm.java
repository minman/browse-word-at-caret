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

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class BWACConfigurationForm {
    private JCheckBox cbMarkup;
    private JCheckBox cbHighlight;
    private JPanel mainPanel;
    private JPanel highlightColorPanel;
    private JCheckBox cbSelectWord;
    private JPanel markupColorPanel;

    // cache current color as Color-Class (instead String) -> (for faster access in method isModified())
    private Color highlightColor;
    private Color markupColor;
    private JButton btRestore;

    public BWACConfigurationForm() {
        // highlightColorPanel
        highlightColorPanel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
        highlightColorPanel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent mouseEvent) {
                Color color = JColorChooser.showDialog(mainPanel, "Highlight color", highlightColorPanel.getBackground());
                if (color != null) {
                    highlightColorPanel.setBackground(color);
                }
            }
        });
        // markupColorPanel
        markupColorPanel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
        markupColorPanel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent mouseEvent) {
                Color color = JColorChooser.showDialog(mainPanel, "Stripe Mark color", markupColorPanel.getBackground());
                if (color != null) {
                    markupColorPanel.setBackground(color);
                }
            }
        });
        // btRestore
        btRestore.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                restoreDefaults();
            }
        });
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    private void restoreDefaults() {
        // markup
        cbMarkup.setSelected(BWACApplicationComponent.defaultPrefShowMarkup);
        markupColorPanel.setBackground(BWACApplicationComponent.defaultPrefColorMarkup);
        // highlight
        cbHighlight.setSelected(BWACApplicationComponent.defaultPrefShowHighlight);
        highlightColorPanel.setBackground(BWACApplicationComponent.defaultPrefColorHighlight);
        // selectWord
        cbSelectWord.setSelected(BWACApplicationComponent.defaultPrefSelectWord);
    }

    public boolean isModified(BWACApplicationComponent applicationComponent) {
        return (applicationComponent.prefShowMarkup != cbMarkup.isSelected()) ||
                (applicationComponent.prefShowHighlight != cbHighlight.isSelected()) ||
                (applicationComponent.prefSelectWord != cbSelectWord.isSelected()) ||
                (!markupColor.equals(markupColorPanel.getBackground())) ||
                (!highlightColor.equals(highlightColorPanel.getBackground()));
    }

    public void getData(BWACApplicationComponent applicationComponent) {
        // markup
        markupColor = markupColorPanel.getBackground();
        applicationComponent.prefShowMarkup = cbMarkup.isSelected();
        applicationComponent.prefColorMarkup = BWACApplicationComponent.ColorToString(markupColor);
        // highlight
        highlightColor = highlightColorPanel.getBackground();
        applicationComponent.prefShowHighlight = cbHighlight.isSelected();
        applicationComponent.prefColorHighlight = BWACApplicationComponent.ColorToString(highlightColor);
        // selectWord
        applicationComponent.prefSelectWord = cbSelectWord.isSelected();
    }

    public void setData(BWACApplicationComponent applicationComponent) {
        // markup
        cbMarkup.setSelected(applicationComponent.prefShowMarkup);
        markupColor = BWACApplicationComponent.StringToColor(applicationComponent.prefColorMarkup);
        if (markupColor == null) {
            markupColor = BWACApplicationComponent.defaultPrefColorMarkup;
        }
        markupColorPanel.setBackground(markupColor);
        // highlight
        cbHighlight.setSelected(applicationComponent.prefShowHighlight);
        highlightColor = BWACApplicationComponent.StringToColor(applicationComponent.prefColorHighlight);
        if (highlightColor == null) {
            highlightColor = BWACApplicationComponent.defaultPrefColorHighlight;
        }
        highlightColorPanel.setBackground(highlightColor);
        // selectWord
        cbSelectWord.setSelected(applicationComponent.prefSelectWord);
    }
}
