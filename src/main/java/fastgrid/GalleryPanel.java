package fastgrid;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class GalleryPanel extends JPanel {

    public float columns = 5f;
    public float targetColumns = 5f;
    public float gapScale = 1f;
    public final float baseGap = 10f;

    public final LayoutController layout;
    public final InteractionController input;
    public final AnimationController anim;

    public GalleryPanel() {

        setOpaque(true);
        setBackground(Color.BLACK);
        setDoubleBuffered(false);
        setFocusable(true);

        layout = new LayoutController(this);
        anim   = new AnimationController(this, layout);
        input  = new InteractionController(this, layout, anim);

        // Keyboard shortcuts
        InputMap im = getInputMap(WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();

        im.put(KeyStroke.getKeyStroke("1"), "grid");
        im.put(KeyStroke.getKeyStroke("2"), "masonry");
        im.put(KeyStroke.getKeyStroke("3"), "gallery");

        am.put("grid", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) {
                layout.setMode(LayoutMode.GRID);
                anim.animateLayout(0f);
            }
        });

        am.put("masonry", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) {
                layout.setMode(LayoutMode.MASONRY);
                anim.animateLayout(1f);
            }
        });

        am.put("gallery", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) {
                layout.setMode(LayoutMode.GALLERY);
                anim.animateLayout(2f);
            }
        });

        addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) {
                layout.invalidate();
                revalidate();
                repaint();
            }
        });

        addMouseListener(input.mouseListener);
        addMouseMotionListener(input.mouseMotionListener);
        addMouseWheelListener(input.wheelListener);
    }

    @Override
    public Dimension getPreferredSize() {
        return layout.getPreferredSize();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        List<Rect> rects = layout.getRects();
        List<Cell> cells = layout.getCells();

        Graphics2D g2 = (Graphics2D) g;

        for (int i = 0; i < cells.size(); i++) {
            Rect r = rects.get(i);

            Cell c = cells.get(i);
            c.outer.setRect(r.x, r.y, r.w, r.h);
            RatioUtil.fit(c.outer, c.inner, c.ratioW, c.ratioH);

            CellRenderer.render(g2, c);
        }
    }
}
