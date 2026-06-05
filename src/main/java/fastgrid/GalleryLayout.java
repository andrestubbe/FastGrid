package fastgrid;

import java.util.List;

public final class GalleryLayout implements LayoutAlgorithm {

    @Override
    public LayoutMeasure measure(List<Cell> cells, LayoutContext ctx) {
        float targetH = ctx.columnWidth();
        float y = ctx.gap();
        int index = 0;
        int n = cells.size();

        while (index < n) {
            GalleryRows.Row row = GalleryRows.packRow(cells, index, targetH, ctx.gap(), ctx.width(), ctx.minSize());
            y += row.rowH() + ctx.gap();
            index += row.count();
        }

        return new LayoutMeasure(y);
    }

    @Override
    public void arrange(List<Cell> cells, LayoutContext ctx, LayoutMeasure m, Rect[] out) {
        float targetH = ctx.columnWidth();
        float gap = ctx.gap();

        float y = gap;
        int index = 0;
        int n = cells.size();

        while (index < n) {
            GalleryRows.Row row = GalleryRows.packRow(cells, index, targetH, gap, ctx.width(), ctx.minSize());
            int rowStart = index;
            int count = row.count();
            float rowH = row.rowH();
            float scale = row.scale();

            // Last row: do NOT justify — left-align it
            boolean isLastRow = (index + count >= n);
            float x = gap;

            for (int i = 0; i < count; i++) {
                Cell c = cells.get(rowStart + i);
                float baseW = LayoutMath.widthFor(c, targetH, ctx.minSize());
                float w = isLastRow ? baseW : baseW * scale;

                out[rowStart + i].set(x, y, w, isLastRow ? targetH : rowH);
                x += w + gap;
            }

            y += rowH + gap;
            index += count;
        }
    }
}
