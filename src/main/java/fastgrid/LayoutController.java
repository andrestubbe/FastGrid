package fastgrid;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LayoutController {

    private final GalleryPanel panel;

    private final List<Cell> cells = new ArrayList<>();
    private final Random rnd = new Random();

    private final LayoutAlgorithm gridAlgo = new GridLayout();
    private final LayoutAlgorithm masonryAlgo = new MasonryLayout();
    private final LayoutAlgorithm galleryAlgo = new GalleryLayout();

    private LayoutAlgorithm currentAlgo = gridAlgo;
    private LayoutAlgorithm targetAlgo = gridAlgo;

    private List<Rect> gridRects = new ArrayList<>();
    private List<Rect> masonryRects = new ArrayList<>();
    private List<Rect> galleryRects = new ArrayList<>();

    private LayoutMode currentMode = LayoutMode.GRID;
    private LayoutMode targetMode = LayoutMode.GRID;

    private float layoutT = 0f;

    public LayoutController(GalleryPanel panel) {
        this.panel = panel;

        float[][] RATIOS = {
                {1, 1}, {4, 3}, {3, 4}, {16, 9}, {9, 16}, {3, 2}, {2, 3}, {4, 16}, {12, 16}, {16, 6}
        };

        for (int i = 0; i < 500; i++) {
            float[] r = RATIOS[rnd.nextInt(RATIOS.length)];
            cells.add(new Cell(r[0], r[1]));
        }
    }

    public List<Cell> getCells() {
        return cells;
    }

    public void setLayoutT(float t) {
        this.layoutT = t;
    }

    public float getLayoutT() {
        return layoutT;
    }

    public void invalidate() {
        gridRects.clear();
        masonryRects.clear();
        galleryRects.clear();
    }

    public Dimension getPreferredSize() {
        LayoutContext ctx = createContext();
        LayoutMeasure m = currentAlgo.measure(cells, ctx);
        return new Dimension((int) ctx.width(), (int) m.height());
    }

    public void setMode(LayoutMode mode) {

        targetMode = mode;

        targetAlgo = switch (mode) {
            case GRID -> gridAlgo;
            case MASONRY -> masonryAlgo;
            case GALLERY -> galleryAlgo;
        };

        computeAll();
    }

    public List<Rect> getRects() {
        ensureComputed();
        return interpolate();
    }

    private LayoutContext createContext() {
        float width = panel.getWidth() > 0 ? panel.getWidth() : 700;
        float gap = Math.max(2f, panel.baseGap * panel.gapScale);
        float minSize = 24f;
        float cols = panel.columns;

        return LayoutContext.of(width, gap, minSize, cols);
    }

    private void ensureComputed() {
        if (gridRects.isEmpty()) gridRects = compute(gridAlgo);
        if (masonryRects.isEmpty()) masonryRects = compute(masonryAlgo);
        if (galleryRects.isEmpty()) galleryRects = compute(galleryAlgo);
    }

    private void computeAll() {
        gridRects = compute(gridAlgo);
        masonryRects = compute(masonryAlgo);
        galleryRects = compute(galleryAlgo);
    }

    private List<Rect> compute(LayoutAlgorithm algo) {
        LayoutContext ctx = createContext();
        LayoutMeasure m = algo.measure(cells, ctx);
        panel.setPreferredSize(new Dimension((int) ctx.width(), (int) m.height()));
        return algo.arrange(cells, ctx, m);
    }

    private List<Rect> interpolate() {
        List<Rect> out = new ArrayList<>(cells.size());

        for (int i = 0; i < cells.size(); i++) {

            Rect a = gridRects.get(i);
            Rect b = masonryRects.get(i);
            Rect c = galleryRects.get(i);

            float t = layoutT;
            Rect r;

            if (t <= 1f)
                r = Rect.lerp(a, b, t);
            else
                r = Rect.lerp(b, c, t - 1f);

            out.add(r);
        }

        return out;
    }
}

