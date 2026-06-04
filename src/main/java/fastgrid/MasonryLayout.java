package fastgrid;

import java.util.*;

public final class MasonryLayout implements LayoutAlgorithm {

    private static final class Column {
        int index;
        float height;

        Column(int index, float height) {
            this.index = index;
            this.height = height;
        }
    }

    @Override
    public LayoutMeasure measure(List<Cell> cells, LayoutContext ctx) {
        int cols = Math.round(ctx.columns());
        float gap = ctx.gap();
        float colW = ctx.columnWidth();

        PriorityQueue<Column> pq = new PriorityQueue<>(Comparator.comparing(c -> c.height));
        for (int i = 0; i < cols; i++) pq.add(new Column(i, gap));

        for (Cell c : cells) {
            Column col = pq.poll();
            float h = LayoutMath.heightFor(c, colW, ctx.minSize());
            col.height += h + gap;
            pq.add(col);
        }

        float max = 0f;
        for (Column c : pq) max = Math.max(max, c.height);

        return new LayoutMeasure(max + gap);
    }

    @Override
    public List<Rect> arrange(List<Cell> cells, LayoutContext ctx, LayoutMeasure m) {
        int cols = Math.round(ctx.columns());
        float gap = ctx.gap();
        float colW = ctx.columnWidth();

        float[] heights = new float[cols];
        Arrays.fill(heights, gap);

        List<Rect> out = new ArrayList<>(cells.size());

        for (Cell c : cells) {
            int col = LayoutMath.minIndex(heights);
            float h = LayoutMath.heightFor(c, colW, ctx.minSize());

            float x = gap + col * (colW + gap);
            float y = heights[col];

            out.add(new Rect(x, y, colW, h));
            heights[col] += h + gap;
        }

        return out;
    }
}

