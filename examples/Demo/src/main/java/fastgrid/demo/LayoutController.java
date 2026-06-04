package fastgrid.demo;

import fastgrid.*;
import fastgrid.GridLayout;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LayoutController {

    private final FastGridView panel;

    private List<Cell> cells = new ArrayList<>();

    private final LayoutAlgorithm gridAlgo    = new GridLayout();
    private final LayoutAlgorithm masonryAlgo = new MasonryLayout();
    private final LayoutAlgorithm galleryAlgo = new GalleryLayout();

    private LayoutAlgorithm currentAlgo = gridAlgo;

    private Rect[] sourceBounds;
    private Rect[] targetBounds;
    private Rect[] currentBounds;

    private boolean initialized = false;
    private float layoutT = 0f;

    public LayoutController(FastGridView panel) {
        this.panel = panel;
    }

    public void setCells(List<Cell> newCells) {
        this.cells = newCells;
        this.initialized = false; // force reallocation
    }

    public List<Cell> getCells() { return cells; }

    public float getLayoutT() { return layoutT; }

    public void setLayoutT(float t) {
        this.layoutT = t;
    }

    public void invalidate() {
        initialized = false;
    }

    public Dimension getPreferredSize() {
        if (cells.isEmpty()) return new Dimension(0, 0);
        LayoutContext ctx = createContext();
        LayoutMeasure m = currentAlgo.measure(cells, ctx);
        return new Dimension((int) ctx.width(), (int) m.height());
    }

    public void setMode(LayoutMode mode) {
        ensureInitialized();
        if (cells.isEmpty()) return;

        for (int i = 0; i < currentBounds.length; i++) {
            sourceBounds[i].set(currentBounds[i]);
        }

        currentAlgo = switch (mode) {
            case GRID    -> gridAlgo;
            case MASONRY -> masonryAlgo;
            case GALLERY -> galleryAlgo;
        };

        List<Rect> newTarget = compute(currentAlgo);
        for (int i = 0; i < targetBounds.length; i++) {
            targetBounds[i].set(newTarget.get(i));
        }

        layoutT = 0f;
    }

    public Rect[] getRects() {
        if (cells.isEmpty()) return new Rect[0];
        ensureInitialized();
        updateInterpolation();
        return currentBounds;
    }

    private LayoutContext createContext() {
        float width   = panel.getWidth() > 0 ? panel.getWidth() : 700;
        float gap     = Math.max(2f, panel.baseGap * panel.gapScale);
        float minSize = 24f;
        float cols    = panel.columns;
        return LayoutContext.of(width, gap, minSize, cols);
    }

    private void ensureInitialized() {
        if (initialized || cells.isEmpty()) return;

        int n = cells.size();
        sourceBounds  = allocRects(n);
        targetBounds  = allocRects(n);
        currentBounds = allocRects(n);

        List<Rect> initRects = compute(currentAlgo);
        for (int i = 0; i < n; i++) {
            sourceBounds[i].set(initRects.get(i));
            targetBounds[i].set(initRects.get(i));
            currentBounds[i].set(initRects.get(i));
        }

        layoutT = 1f;
        initialized = true;
    }

    private void updateInterpolation() {
        float t = Math.min(1f, Math.max(0f, layoutT));
        for (int i = 0; i < currentBounds.length; i++) {
            currentBounds[i].setLerp(sourceBounds[i], targetBounds[i], t);
        }
    }

    private List<Rect> compute(LayoutAlgorithm algo) {
        LayoutContext ctx = createContext();
        LayoutMeasure m   = algo.measure(cells, ctx);
        return algo.arrange(cells, ctx, m);
    }

    private static Rect[] allocRects(int n) {
        Rect[] arr = new Rect[n];
        for (int i = 0; i < n; i++) arr[i] = new Rect();
        return arr;
    }
}
