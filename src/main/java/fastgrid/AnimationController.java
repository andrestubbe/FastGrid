package fastgrid;

public class AnimationController {

    private final GalleryPanel panel;
    private final LayoutController layout;

    private MiniTween layoutTween;
    private MiniTween columnTween;

    public AnimationController(GalleryPanel panel, LayoutController layout) {
        this.panel = panel;
        this.layout = layout;
    }

    public void animateLayout(float target) {

        if (layoutTween != null)
            layoutTween = null;

        float start = layout.getLayoutT();
        float end = target;

        layoutTween = new MiniTween(start, end, 350)
                .onUpdate(() -> {
                    if (layoutTween == null) return;   // â† CRASH FIX
                    layout.setLayoutT(layoutTween.getValue());
                    panel.revalidate();
                    panel.repaint();
                })
                .onEnd(() -> {
                    if (layoutTween == null) return;   // â† CRASH FIX
                    layout.setLayoutT(end);
                    layoutTween = null;
                    panel.revalidate();
                    panel.repaint();
                });

        layoutTween.start();
    }

    // ------------------------------------------------------------
    // COLUMN SNAP (nur Columns, kein GAP mehr anfassen)
    // ------------------------------------------------------------
    public void snapColumns(float currentColumns) {

        float snapped = Math.max(2f, Math.round(currentColumns));
        float start = currentColumns;
        float end = snapped;

        if (columnTween != null)
            columnTween = null;

        columnTween = new MiniTween(start, end, 260)
                .onUpdate(() -> {
                    if (columnTween == null) return;   // â† CRASH FIX

                    float v = columnTween.getValue();
                    panel.columns = v;
                    panel.targetColumns = v;

                    layout.invalidate();
                    panel.revalidate();
                    panel.repaint();
                })
                .onEnd(() -> {
                    if (columnTween == null) return;   // â† CRASH FIX

                    panel.columns = end;
                    panel.targetColumns = end;

                    columnTween = null;
                    layout.invalidate();
                    panel.revalidate();
                    panel.repaint();
                });

        columnTween.start();
    }
}

