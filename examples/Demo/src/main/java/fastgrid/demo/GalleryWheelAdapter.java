package fastgrid.demo;

import fastgrid.*;

import java.awt.event.*;

public class GalleryWheelAdapter implements MouseWheelListener {

    private final FastGridView panel;
    private final LayoutController layout;
    private final AnimationController anim;
    private final InteractionController input;

    public GalleryWheelAdapter(FastGridView panel, LayoutController layout, AnimationController anim, InteractionController input) {
        this.panel  = panel;
        this.layout = layout;
        this.anim   = anim;
        this.input  = input;
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {

        // ALT = GAP SCALING
        if (e.isAltDown()) {
            panel.gapScale += e.getWheelRotation() * 0.05f;
            panel.gapScale = Math.max(0.1f, Math.min(5f, panel.gapScale));
            layout.invalidate();
            panel.repaint();
            return;
        }

        // SHIFT = COLUMN SCALING
        if (e.isShiftDown()) {
            panel.columns -= e.getWheelRotation() * 0.25f;
            panel.columns = Math.max(2f, Math.min(40f, panel.columns));
            panel.targetColumns = panel.columns;
            layout.invalidate();
            panel.repaint();
            return;
        }

        // NORMAL SCROLL (smooth wheel)
        input.addScrollVelocity(-e.getWheelRotation() * 400f);
    }
}
