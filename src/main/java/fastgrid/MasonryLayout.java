package fastgrid;

import java.util.Arrays;
import java.util.List;

public final class MasonryLayout implements LayoutAlgorithm {

    @Override
    public LayoutMeasure measure(List<Cell> cells, LayoutContext ctx) {
        int cols = Math.max(1, Math.round(ctx.columns()));
        float gap = ctx.gap();
        
        float usable = Math.max(1f, ctx.width() - gap * (cols + 1f));
        float colW = Math.max(ctx.minSize(), usable / cols);

        // Plain float array — linear min search is faster than PriorityQueue for typical col counts
        float[] heights = new float[cols];
        Arrays.fill(heights, gap);

        for (Cell c : cells) {
            int col = LayoutMath.minIndex(heights);
            float h = LayoutMath.heightFor(c, colW, ctx.minSize());
            heights[col] += h + gap;
        }

        float max = 0f;
        for (float h : heights) max = Math.max(max, h);
        return new LayoutMeasure(max + gap);
    }

    @Override
    public List<Rect> arrange(List<Cell> cells, LayoutContext ctx, LayoutMeasure m) {
        int cols = Math.max(1, Math.round(ctx.columns()));
        float gap = ctx.gap();
        
        float usable = Math.max(1f, ctx.width() - gap * (cols + 1f));
        float colW = Math.max(ctx.minSize(), usable / cols);

        float[] heights = new float[cols];
        Arrays.fill(heights, gap);

        Rect[] out = new Rect[cells.size()];
        for (int i = 0; i < cells.size(); i++) {
            Cell c = cells.get(i);
            int col = LayoutMath.minIndex(heights);
            float h = LayoutMath.heightFor(c, colW, ctx.minSize());
            float x = gap + col * (colW + gap);
            float y = heights[col];
            out[i] = new Rect(x, y, colW, h);
            heights[col] += h + gap;
        }

        return Arrays.asList(out);
    }
}
