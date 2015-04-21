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

package org.yasas.maglass;

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.beans.*;

public class Maglass {
  //<editor-fold desc="Messages">
  private static final String EXC_ARGUMENT_0_MUST_NOT_BE_NULL = "Argument ''%s'' must not be null.";
  private static final String EXC_ARGUMENT_MUST_NOT_BE_NULL   = "Argument must not be null.";
  //</editor-fold>

  private Maglass() { }

  public static BufferedImage createImage(JComponent component, Rectangle region) {
    final BufferedImage image = new BufferedImage(region.width, region.height, BufferedImage.TYPE_INT_RGB);

    final Graphics2D g = image.createGraphics();
    try {
      if (!component.isOpaque()) {
        g.setColor(component.getBackground());
        g.fillRect(region.x, region.y, region.width, region.height);
      }
      g.translate(- region.x, - region.y);

      component.paint(g);
    } finally {
      g.dispose();
    }

    return image;
  }

  public static <T> T notNull(T arg, String argName) throws IllegalArgumentException {
    if (arg == null) {
      if (argName != null) {
        throw new IllegalArgumentException(String.format(EXC_ARGUMENT_0_MUST_NOT_BE_NULL, argName));
      }
      throw new IllegalArgumentException(EXC_ARGUMENT_MUST_NOT_BE_NULL);
    }
    return arg;
  }

  public static class PropertyChangeSupportOnEDT extends PropertyChangeSupport {
    //<editor-fold desc="Argument ID's">
    private static final String ARG_EVT         = "evt";
    private static final String ARG_SOURCE_BEAN = "sourceBean";
    //</editor-fold>

    private boolean notifyOnEDT;

    //<editor-fold desc="Constructors">
    public PropertyChangeSupportOnEDT(Object sourceBean) {
      this(sourceBean, true);
    }

    public PropertyChangeSupportOnEDT(Object sourceBean, boolean notifyOnEDT) {
      super(
        notNull(sourceBean, ARG_SOURCE_BEAN)
      );
      this.notifyOnEDT = notifyOnEDT;
    }
    //</editor-fold>

    /**
     * {@inheritDoc}
     */
    @Override
    public void firePropertyChange(final PropertyChangeEvent evt) {
      notNull(evt, ARG_EVT);

      if (!notifyOnEDT || SwingUtilities.isEventDispatchThread()) {
        super.firePropertyChange(evt);
      } else {
        SwingUtilities.invokeLater(new Runnable() {
          @Override
          public void run() {
            PropertyChangeSupportOnEDT.super.firePropertyChange(evt);
          }
        });
      }
    }
  }

  public static enum RenderQuality {
    Speed, Default, Quality
  }
}
