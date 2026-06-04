package fastgrid;

import java.util.List;

public final class GalleryRows {

    public static Row packRow(
            List<Cell> cells,
            int start,
            float targetHeight,
            float gap,
            float width,
            float minSize
    ) {
        float rowSum = 0f;
        int count = 0;
        int i = start;

        while (i < cells.size()) {
            float w = LayoutMath.widthFor(cells.get(i), targetHeight, minSize);
            float next = rowSum + w;
            float gaps = gap * (count + 2);

            if (count > 0 && next + gaps > width)
                break;

            rowSum = next;
            count++;
            i++;
        }

        if (count == 0) {
            count = 1;
            rowSum = LayoutMath.widthFor(cells.get(start), targetHeight, minSize);
            i = start + 1;
        }

        float gaps = gap * (count + 1);
        float targetContent = width - gaps;
        float scale = targetContent / rowSum;
        float rowH = targetHeight * scale;

        return new Row(start, count, rowH, scale);
    }
}
