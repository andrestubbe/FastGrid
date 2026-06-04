package fastgrid;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class InteractionController {

    private GalleryPanel panel;
    private LayoutController layout;
    private AnimationController anim;

    private int lastX = 0;
    private int lastY = 0;

    private boolean isColumnScaling = false;
    private boolean isGapScaling = false;
    private boolean isScrolling = false;

    private float velocityY = 0f;
    private long lastDragTime = 0;
    private Timer momentumTimer;

    public final MouseListener mouseListener;
    public final MouseMotionListener mouseMotionListener;
    public final MouseWheelListener wheelListener;

    public InteractionController(GalleryPanel panel, LayoutController layout, AnimationController anim) {
        this.panel = panel;
        this.layout = layout;
        this.anim = anim;

        this.mouseListener = createMouseListener();
        this.mouseMotionListener = createMouseMotionListener();
        this.wheelListener = new GalleryWheelAdpater(panel, layout, anim);
    }

    private MouseListener createMouseListener() {
        return new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                lastX = e.getX();
                lastY = e.getY();
                lastDragTime = System.nanoTime();
                velocityY = 0f;

                isColumnScaling = false;
                isGapScaling = false;
                isScrolling = false;

                stopMomentum();
            }

            @Override
            public void mouseReleased(MouseEvent e) {

                if (isColumnScaling) {
                    anim.snapColumns(panel.columns);
                }

                startMomentum();

                isColumnScaling = false;
                isGapScaling = false;
                isScrolling = false;
            }
        };
    }

    private MouseMotionListener createMouseMotionListener() {
        return new MouseMotionAdapter() {

            @Override
            public void mouseDragged(MouseEvent e) {

                long now = System.nanoTime();
                float dt = (now - lastDragTime) / 1_000_000f;
                lastDragTime = now;

                int dx = e.getX() - lastX;
                int dy = e.getY() - lastY;

                lastX = e.getX();
                lastY = e.getY();

                // --------------------------------------------------------
                // ALT = GAP SCALING
                // --------------------------------------------------------
                if (e.isAltDown()) {

                    isGapScaling = true;

                    panel.gapScale += dx * 0.01f;
                    panel.gapScale = Math.max(0.1f, Math.min(5f, panel.gapScale));

                    layout.invalidate();
                    panel.revalidate();
                    panel.repaint();
                    return;
                }

                // --------------------------------------------------------
                // SHIFT = COLUMN SCALING
                // --------------------------------------------------------
                if (e.isShiftDown()) {

                    isColumnScaling = true;

                    float oldCols = panel.columns;

                    panel.columns -= dx * 0.02f;
                    panel.columns = Math.max(2f, Math.min(40f, panel.columns));
                    panel.targetColumns = panel.columns;

                    layout.invalidate();
                    panel.revalidate();
                    panel.repaint();
                    return;
                }

                // --------------------------------------------------------
                // NORMAL SCROLL
                // --------------------------------------------------------
                isScrolling = true;

                JViewport vp = (JViewport) panel.getParent();
                Point p = vp.getViewPosition();

                p.y -= dy;
                if (p.y < 0) p.y = 0;

                vp.setViewPosition(p);

                velocityY = (dy / dt) * 16f;
            }
        };
    }

    private void stopMomentum() {
        if (momentumTimer != null) {
            momentumTimer.stop();
            momentumTimer = null;
        }
    }

    private void startMomentum() {
        if (!isScrolling) return;

        stopMomentum();

        momentumTimer = new Timer(16, e -> {

            if (Math.abs(velocityY) < 0.1f) {
                stopMomentum();
                return;
            }

            JViewport vp = (JViewport) panel.getParent();
            Point p = vp.getViewPosition();

            p.y -= velocityY * 0.016f;
            if (p.y < 0) p.y = 0;

            vp.setViewPosition(p);

            velocityY *= 0.92f;
        });

        momentumTimer.start();
    }
}
