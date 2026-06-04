package fastgrid;

import java.util.ArrayList;
import java.util.List;

public final class GridLayout implements LayoutAlgorithm {

    @Override
    public LayoutMeasure measure(List<Cell> cells, LayoutContext ctx) {
        float cols = ctx.columns();
        float size = ctx.columnWidth();
        float rows = (float) Math.ceil(cells.size() / cols);
        float height = rows * (size + ctx.gap()) + ctx.gap();
        return new LayoutMeasure(height);
    }

    @Override
    public List<Rect> arrange(List<Cell> cells, LayoutContext ctx, LayoutMeasure m) {
        List<Rect> out = new ArrayList<>(cells.size());

        float size = ctx.columnWidth();
        float gap = ctx.gap();
        float width = ctx.width();

        float x = gap;
        float y = gap;

        for (int i = 0; i < cells.size(); i++) {
            out.add(new Rect(x, y, size, size));

            x += size + gap;
            if (x + size > width + 0.5f) {
                x = gap;
                y += size + gap;
            }
        }

        return out;
    }
}
