/*
 *  Copyright (c) 2013-2015 Stepan Adamec (adamec@yasas.org)
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */

package org.yasas.maglass.demo;

import org.yasas.maglass.*;

import javax.swing.*;
import javax.swing.plaf.nimbus.*;
import java.awt.*;

public class DemoFrame extends JFrame {
  public DemoFrame() throws HeadlessException {
    super("Maglass demo");

    final JLayer<Component> layer = new JLayer<>(
      createContent(), new MaglassUI<>()
    );
    configureMagnifier((MaglassUI<?>) layer.getUI());

    getContentPane().add(layer);

    pack();

    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setLocationByPlatform(true);
  }

  private JPanel createContent() {
    return new JPanel(new BorderLayout()) {{
      final JTable table;

      final Object[][] rowData = { {"One", 1.0d}, {"Two", 2.0d} , {"Three", 3.0d} , {"Four", 4.0d} };
      final String[] columnNames = { "Key", "Value" };

      add(
        new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
          new JSplitPane(JSplitPane.VERTICAL_SPLIT,
            new JScrollPane(table = new JTable(rowData, columnNames)),
            new JLabel(new ImageIcon(getClass().getResource("/coffee-171653_320.jpg")))
          ),
          new JScrollPane(new JTree())
        )
      );
      table.setFillsViewportHeight(true);
      table.setPreferredScrollableViewportSize(new Dimension(300, 150));
    }};
  }

  private MaglassUI<?> configureMagnifier(MaglassUI<?> maglassUI) {
    maglassUI.setActive(true);
    maglassUI.setAntialiasing(true);
    maglassUI.setRadius(155);
    maglassUI.setRenderQuality(Maglass.RenderQuality.Speed);
    maglassUI.setZoomFactor(2.0d);

    return maglassUI;
  }

  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      @Override
      public void run() {
        try {
          UIManager.setLookAndFeel(NimbusLookAndFeel.class.getName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
          System.err.println("Unable to set Look&Feel: " + e.getLocalizedMessage());
        }

        new DemoFrame().setVisible(true);
      }
    });
  }
}
