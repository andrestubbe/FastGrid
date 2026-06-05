package fastgrid.demo;

import fastgrid.*;

import java.awt.event.*;

public class InteractionController {

    private FastGridView panel;
    private LayoutController layout;
    private AnimationController anim;

    private int lastX = 0;
    private int lastY = 0;

    private boolean isColumnScaling = false;
    private boolean isGapScaling    = false;
    private boolean isScrolling     = false;

    private float velocityY   = 0f;
    private long lastDragTime = 0;
    private long lastTickTime = 0;
    private javax.swing.Timer momentumTimer;

    public final MouseListener mouseListener;
    public final MouseMotionListener mouseMotionListener;
    public final MouseWheelListener wheelListener;

    public InteractionController(FastGridView panel, LayoutController layout, AnimationController anim) {
        this.panel  = panel;
        this.layout = layout;
        this.anim   = anim;

        this.mouseListener       = createMouseListener();
        this.mouseMotionListener = createMouseMotionListener();
        this.wheelListener       = new GalleryWheelAdapter(panel, layout, anim, this);
    }

    public void addScrollVelocity(float v) {
        isScrolling = true;
        velocityY += v;
        if (momentumTimer == null || !momentumTimer.isRunning()) {
            startMomentum();
        }
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
                isGapScaling    = false;
                isScrolling     = false;

                stopMomentum();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (isColumnScaling) {
                    anim.snapColumns(panel.columns);
                }
                startMomentum();
                isColumnScaling = false;
                isGapScaling    = false;
                isScrolling     = false;
            }
        };
    }

    private MouseMotionListener createMouseMotionListener() {
        return new MouseMotionAdapter() {

            @Override
            public void mouseDragged(MouseEvent e) {

                long now = System.nanoTime();
                float dtSec = (now - lastDragTime) / 1_000_000_000f;
                lastDragTime = now;

                int dx = e.getX() - lastX;
                int dy = e.getY() - lastY;

                lastX = e.getX();
                lastY = e.getY();

                // ALT = GAP SCALING
                if (e.isAltDown()) {
                    isGapScaling = true;
                    panel.gapScale -= dx * 0.01f;
                    panel.gapScale = Math.max(0f, Math.min(5f, panel.gapScale));
                    layout.invalidate();
                    panel.repaint();
                    return;
                }

                // SHIFT = COLUMN SCALING
                if (e.isShiftDown()) {
                    isColumnScaling = true;
                    panel.columns += dx * 0.02f;
                    panel.columns = Math.max(2f, Math.min(40f, panel.columns));
                    panel.targetColumns = panel.columns;
                    layout.invalidate();
                    panel.repaint();
                    return;
                }

                // NORMAL SCROLL (drag)
                isScrolling = true;
                panel.scroll(dy);
                velocityY = (dtSec > 0 && dtSec < 0.2f) ? (dy / dtSec) : 0f;
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

        lastTickTime = System.nanoTime();
        momentumTimer = new javax.swing.Timer(16, e -> {
            long now = System.nanoTime();
            float dtSec = (now - lastTickTime) / 1_000_000_000f;
            lastTickTime = now;

            // Cap dt to prevent massive jumps if timer stalls
            if (dtSec > 0.1f) dtSec = 0.016f;

            if (Math.abs(velocityY) < 10f) {
                stopMomentum();
                return;
            }

            // Apply friction relative to time passed (0.92 per 16ms = ~0.005 per second decay)
            float friction = (float) Math.pow(0.92, dtSec / 0.016f);
            
            panel.scroll(velocityY * dtSec);
            velocityY *= friction;
        });

        momentumTimer.start();
    }
}
