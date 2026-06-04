package fastgrid;

import java.util.List;

public interface LayoutAlgorithm {

    LayoutMeasure measure(List<Cell> cells, LayoutContext ctx);

    List<Rect> arrange(List<Cell> cells, LayoutContext ctx, LayoutMeasure measure);
}

