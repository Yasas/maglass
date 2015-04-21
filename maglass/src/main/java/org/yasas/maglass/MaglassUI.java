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
import javax.swing.plaf.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.beans.*;

public class MaglassUI<V extends Component> extends LayerUI<V> {
  //<editor-fold desc="Constants">
  private static final int keyMask = KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK;
  private static final BasicStroke stroke = new BasicStroke(2.0f);
  //</editor-fold>

  //<editor-fold desc="Property names">
  private static final String PROPERTY_ACTIVE         = "active";
  private static final String PROPERTY_ANTIALIASING   = "antialiasing";
  private static final String PROPERTY_RADIUS         = "radius";
  private static final String PROPERTY_RENDER_QUALITY = "renderQuality";
  private static final String PROPERTY_ZOOM_FACTOR    = "zoomFactor";
  //</editor-fold>

  //<editor-fold desc="Property fields">
  private boolean active, antialiasing;
  private int radius;
  private Maglass.RenderQuality renderQuality;
  private double zoomFactor;
  //</editor-fold>

  //<editor-fold desc="Fields">
  private Component view;
  private boolean keysDown = false;
  private Point mouseAt;
  private Dimension s, s_2;
  private Rectangle r, r_2;
  private int brdIns_1 = 1, brdIns_2 = brdIns_1 * 2;
  private int clpIns_1 = 2, clpIns2 = clpIns_1 * 2;
  private Object renderQualityHint;
  //</editor-fold>

  public MaglassUI() {
    setActive(false);
    setAntialiasing(true);
    setRadius(150);
    setRenderQuality(Maglass.RenderQuality.Default);
    setZoomFactor(2.0d);
  }

  //<editor-fold desc="Properties">
  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    if (isActive() != active) {
      final PropertyChangeEvent evt = new PropertyChangeEvent(
        this, PROPERTY_ACTIVE, isActive(), active
      );
      this.active = active;

      firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
    }
  }

  public boolean isAntialiasing() {
    return antialiasing;
  }

  public void setAntialiasing(boolean antialiasing) {
    if (isAntialiasing() != antialiasing) {
      final PropertyChangeEvent evt = new PropertyChangeEvent(
        this, PROPERTY_ANTIALIASING, isAntialiasing(), antialiasing
      );
      this.antialiasing = antialiasing;

      firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
    }
  }

  public int getRadius() {
    return radius;
  }

  public void setRadius(int radius) {
    if (getRadius() != radius) {
      final PropertyChangeEvent evt = new PropertyChangeEvent(
        this, PROPERTY_RADIUS, getRadius(), radius
      );
      this.radius = radius;

      firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
    }
  }

  public Maglass.RenderQuality getRenderQuality() {
    return renderQuality;
  }

  public void setRenderQuality(Maglass.RenderQuality renderQuality) {
    if (getRenderQuality() != renderQuality) {
      final PropertyChangeEvent evt = new PropertyChangeEvent(
        this, PROPERTY_RENDER_QUALITY, getRenderQuality(), renderQuality
      );
      this.renderQuality = renderQuality;

      {
        this.renderQualityHint = (renderQuality == Maglass.RenderQuality.Default) ? RenderingHints.VALUE_RENDER_DEFAULT : (
          (renderQuality == Maglass.RenderQuality.Speed) ? RenderingHints.VALUE_RENDER_SPEED : RenderingHints.VALUE_RENDER_QUALITY
        );
      }

      firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
    }
  }

  public double getZoomFactor() {
    return zoomFactor;
  }

  public void setZoomFactor(double zoomFactor) {
    if (getZoomFactor() != zoomFactor) {
      final PropertyChangeEvent evt = new PropertyChangeEvent(
        this, PROPERTY_ZOOM_FACTOR, getZoomFactor(), zoomFactor
      );
      this.zoomFactor = zoomFactor;

      firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
    }
  }
  //</editor-fold>

  //<editor-fold desc="LayerUI">
  @Override
  public void paint(Graphics g, JComponent c) {
    if ((g instanceof Graphics2D) && active && keysDown && (mouseAt != null)) {
      if (isAntialiasing()) {
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      }
      ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_RENDERING, renderQualityHint);

      super.paint(g, c);

      //<editor-fold desc="Magnify">
      final Graphics2D g2d = (Graphics2D) g;

      final BufferedImage i = Maglass.createImage(
        (JComponent) view, r
      );
      final Image i_2 = i.getScaledInstance(s_2.width, s_2.height, BufferedImage.SCALE_SMOOTH);

      final Ellipse2D.Float border = new Ellipse2D.Float(
        r_2.x - brdIns_1, r_2.y - brdIns_1, r_2.width + brdIns_2, r_2.height + brdIns_2
      );
      final Ellipse2D.Float clip = new Ellipse2D.Float(
        r_2.x - clpIns_1, r_2.y - clpIns_1, r_2.width + clpIns2, r_2.height + clpIns2
      );
      g2d.setClip(clip);

      g2d.drawImage(i_2, r_2.x, r_2.y, null);
      g2d.setStroke(stroke);
      g2d.draw(border);
      //</editor-fold>
    } else {
      super.paint(g, c);
    }
  }

  @Override
  protected void processKeyEvent(KeyEvent evt, JLayer<? extends V> layer) {
    if (active) {
      keysDown = (
        (evt.getModifiersEx() & keyMask) == keyMask
      );

      layer.repaint();
    }
  }

  @Override
  protected void processMouseEvent(MouseEvent evt, JLayer<? extends V> layer) {
    if (active && keysDown) {
      if ((evt.getID() == MouseEvent.MOUSE_ENTERED) || (evt.getID() == MouseEvent.MOUSE_EXITED)) {
        evt.consume();
      }
    }
  }

  @Override
  protected void processMouseMotionEvent(MouseEvent evt, JLayer<? extends V> layer) {
    if (active) {
      Rectangle r_2o = r_2;

      SwingUtilities.convertPointFromScreen(
        mouseAt = evt.getLocationOnScreen(), layer.getView()
      );
      final int f = (int) zoomFactor;

      s   = new Dimension(radius, radius);
      s_2 = new Dimension(f * s.width, f * s.height);

      r   = new Rectangle(mouseAt.x - (s.width / 2), mouseAt.y - (s.height / 2), s.width, s.height);
      r_2 = new Rectangle(mouseAt.x - (s_2.width / 2), mouseAt.y - (s_2.height / 2), s_2.width, s_2.height);

      if (keysDown) {
        r_2o = r_2o.union(r_2);

        layer.paintImmediately(new Rectangle(
          r_2o.x - 5, r_2o.y - 5, r_2o.width + 10, r_2o.height + 10
        ));
        evt.consume();
      }
    }
  }

  @Override
  public void installUI(JComponent c) {
    super.installUI(c);

    ((JLayer<?>) c).setLayerEventMask(
      AWTEvent.MOUSE_MOTION_EVENT_MASK | AWTEvent.KEY_EVENT_MASK | AWTEvent.MOUSE_EVENT_MASK
    );
    view = ((JLayer<?>) c).getView();

    if (!c.isDoubleBuffered()) {
      c.setDoubleBuffered(true);
    }
  }

  @Override
  public void uninstallUI(JComponent c) {
    ((JLayer<?>) c).setLayerEventMask(
      0
    );
    view = null;

    super.uninstallUI(c);
  }
  //</editor-fold>
}
