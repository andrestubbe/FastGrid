package fastgrid;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GalleryWheelAdpater implements MouseWheelListener {

    private final GalleryPanel panel;
    private final LayoutController layout;
    private final AnimationController anim;

    public GalleryWheelAdpater(GalleryPanel panel, LayoutController layout, AnimationController anim) {
        this.panel = panel;
        this.layout = layout;
        this.anim = anim;
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {

        // ALT = GAP SCALING
        if (e.isAltDown()) {

            panel.gapScale += e.getWheelRotation() * 0.05f;
            panel.gapScale = Math.max(0.1f, Math.min(5f, panel.gapScale));

            layout.invalidate();
            panel.revalidate();
            panel.repaint();
            return;
        }

        // SHIFT = COLUMN SCALING
        if (e.isShiftDown()) {

            panel.columns -= e.getWheelRotation() * 0.25f;
            panel.columns = Math.max(2f, Math.min(40f, panel.columns));
            panel.targetColumns = panel.columns;

            layout.invalidate();
            panel.revalidate();
            panel.repaint();
            return;
        }

        // NORMAL SCROLL
        JViewport vp = (JViewport) panel.getParent();
        Point p = vp.getViewPosition();

        p.y += e.getWheelRotation() * 40;
        if (p.y < 0) p.y = 0;

        vp.setViewPosition(p);
    }
}

