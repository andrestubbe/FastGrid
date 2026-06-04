package fastgrid;

import java.util.Arrays;
import java.util.List;

public final class GridLayout implements LayoutAlgorithm {

    @Override
    public LayoutMeasure measure(List<Cell> cells, LayoutContext ctx) {
        int cols = Math.max(1, Math.round(ctx.columns()));
        float gap = ctx.gap();
        float usable = Math.max(1f, ctx.width() - gap * (cols + 1f));
        float size = Math.max(ctx.minSize(), usable / cols);

        float rows = (float) Math.ceil(cells.size() / (double) cols);
        float height = rows * (size + gap) + gap;
        return new LayoutMeasure(height);
    }

    @Override
    public List<Rect> arrange(List<Cell> cells, LayoutContext ctx, LayoutMeasure m) {
        int cols = Math.max(1, Math.round(ctx.columns()));
        float gap = ctx.gap();
        float width = ctx.width();
        
        float usable = Math.max(1f, width - gap * (cols + 1f));
        float size = Math.max(ctx.minSize(), usable / cols);

        Rect[] out = new Rect[cells.size()];
        float x = gap;
        float y = gap;

        for (int i = 0; i < cells.size(); i++) {
            out[i] = new Rect(x, y, size, size);
            x += size + gap;
            if (x + size > width + 0.5f) {
                x = gap;
                y += size + gap;
            }
        }

        return Arrays.asList(out);
    }
}
