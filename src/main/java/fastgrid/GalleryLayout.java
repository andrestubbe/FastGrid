package fastgrid;

import java.util.ArrayList;
import java.util.List;

public class GalleryLayout implements LayoutAlgorithm {

    @Override
    public LayoutMeasure measure(List<Cell> cells, LayoutContext ctx) {
        float targetH = ctx.columnWidth(); // oder ctx.minSize(), je nach Geschmack

        float y = ctx.gap();
        int index = 0;
        int n = cells.size();

        while (index < n) {

            float rowSum = 0f;
            int count = 0;

            while (index < n) {
                Cell c = cells.get(index);
                float w = targetH * (c.ratioW / c.ratioH);
                float next = rowSum + w;
                float gaps = ctx.gap() * (count + 2);

                if (count > 0 && next + gaps > ctx.width())
                    break;

                rowSum = next;
                count++;
                index++;
            }

            if (count == 0) {
                count = 1;
                Cell c = cells.get(index++);
                rowSum = targetH * (c.ratioW / c.ratioH);
            }

            float gaps = ctx.gap() * (count + 1);
            float targetContent = ctx.width() - gaps;
            float scale = targetContent / rowSum;

            float rowH = targetH * scale;
            y += rowH + ctx.gap();
        }

        return new LayoutMeasure(y);
    }

    @Override
    public List<Rect> arrange(List<Cell> cells, LayoutContext ctx, LayoutMeasure m) {
        float targetH = ctx.columnWidth();

        List<Rect> out = new ArrayList<>();
        float y = ctx.gap();
        int index = 0;
        int n = cells.size();

        while (index < n) {

            float rowSum = 0f;
            int rowStart = index;
            int count = 0;

            while (index < n) {
                Cell c = cells.get(index);
                float w = targetH * (c.ratioW / c.ratioH);
                float next = rowSum + w;
                float gaps = ctx.gap() * (count + 2);

                if (count > 0 && next + gaps > ctx.width())
                    break;

                rowSum = next;
                count++;
                index++;
            }

            if (count == 0) {
                count = 1;
                Cell c = cells.get(index++);
                rowSum = targetH * (c.ratioW / c.ratioH);
            }

            float gaps = ctx.gap() * (count + 1);
            float targetContent = ctx.width() - gaps;
            float scale = targetContent / rowSum;

            float rowH = targetH * scale;
            float x = ctx.gap();

            for (int i = 0; i < count; i++) {
                Cell c = cells.get(rowStart + i);
                float baseW = targetH * (c.ratioW / c.ratioH);
                float w = baseW * scale;

                out.add(new Rect(x, y, w, rowH));
                x += w + ctx.gap();
            }

            y += rowH + ctx.gap();
        }

        return out;
    }
}
