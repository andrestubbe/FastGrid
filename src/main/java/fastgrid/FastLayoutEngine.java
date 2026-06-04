package fastgrid;

import java.util.List;

public final class FastLayoutEngine {

    public static LayoutMeasure measure(
            LayoutAlgorithm algo,
            List<Cell> cells,
            float width,
            float gap,
            float minSize,
            float columns
    ) {
        LayoutContext ctx = LayoutContext.of(width, gap, minSize, columns);
        return algo.measure(cells, ctx);
    }

    public static List<Rect> arrange(
            LayoutAlgorithm algo,
            List<Cell> cells,
            float width,
            float gap,
            float minSize,
            float columns
    ) {
        LayoutContext ctx = LayoutContext.of(width, gap, minSize, columns);
        LayoutMeasure m = algo.measure(cells, ctx);
        return algo.arrange(cells, ctx, m);
    }
}
