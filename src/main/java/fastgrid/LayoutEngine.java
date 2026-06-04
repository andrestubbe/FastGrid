package fastgrid;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class LayoutEngine {

    private static final float MIN_SIZE = 24f;

    // ---------------- GRID ----------------

    public static List<Rectangle2D.Float> computeGrid(
            float columns,
            float gap,
            int total,
            float width
    ) {
        List<Rectangle2D.Float> rects = new ArrayList<>(total);

        float cols = Math.max(1f, columns);
        float totalGap = gap * (cols + 1f);
        float usable = Math.max(1f, width - totalGap);
        float size = Math.max(MIN_SIZE, usable / cols);

        float x = gap;
        float y = gap;

        for (int i = 0; i < total; i++) {
            rects.add(new Rectangle2D.Float(x, y, size, size));

            x += size + gap;

            if (x + size > width + 0.5f) {
                x = gap;
                y += size + gap;
            }
        }

        return rects;
    }

    public static float computeGridHeight(
            float columns,
            float gap,
            int total,
            float width
    ) {
        float cols = Math.max(1f, columns);
        float totalGap = gap * (cols + 1f);
        float usable = Math.max(1f, width - totalGap);
        float size = Math.max(MIN_SIZE, usable / cols);
        float rows = (float)Math.ceil(total / cols);
        return rows * (size + gap) + gap;
    }

    // ---------------- MASONRY ----------------

    public static List<Rectangle2D.Float> computeMasonry(
            float columns,
            float gap,
            List<Cell> cells,
            float width
    ) {
        int total = cells.size();
        int cols = Math.max(1, Math.round(columns));

        List<Rectangle2D.Float> rects = new ArrayList<>(total);

        float totalGap = gap * (cols + 1f);
        float usable = Math.max(1f, width - totalGap);
        float colWidth = Math.max(MIN_SIZE, usable / cols);

        float[] heights = new float[cols];
        for (int i = 0; i < cols; i++) heights[i] = gap;

        for (Cell c : cells) {

            float ratio = c.ratioH / c.ratioW;
            float h = Math.max(MIN_SIZE, colWidth * ratio);

            int bestCol = 0;
            for (int i = 1; i < cols; i++) {
                if (heights[i] < heights[bestCol]) bestCol = i;
            }

            float x = gap + bestCol * (colWidth + gap);
            float y = heights[bestCol];

            rects.add(new Rectangle2D.Float(x, y, colWidth, h));

            heights[bestCol] += h + gap;
        }

        return rects;
    }

    public static float computeMasonryHeight(
            float columns,
            float gap,
            List<Cell> cells,
            float width
    ) {
        int cols = Math.max(1, Math.round(columns));

        float totalGap = gap * (cols + 1f);
        float usable = Math.max(1f, width - totalGap);
        float colWidth = Math.max(MIN_SIZE, usable / cols);

        float[] heights = new float[cols];
        for (int i = 0; i < cols; i++) heights[i] = gap;

        for (Cell c : cells) {
            float ratio = c.ratioH / c.ratioW;
            float h = Math.max(MIN_SIZE, colWidth * ratio);

            int bestCol = 0;
            for (int i = 1; i < cols; i++) {
                if (heights[i] < heights[bestCol]) bestCol = i;
            }

            heights[bestCol] += h + gap;
        }

        float max = heights[0];
        for (int i = 1; i < cols; i++) if (heights[i] > max) max = heights[i];
        return max + gap;
    }

    // ---------------- GALLERY (NEU, PERFEKT BÃœNDIG) ----------------

    public static List<Rectangle2D.Float> computeGallery(
            float targetHeight,
            float gap,
            List<Cell> cells,
            float width
    ) {
        List<Rectangle2D.Float> rects = new ArrayList<>();

        float y = gap;
        int index = 0;
        int n = cells.size();

        while (index < n) {

            float rowSum = 0f;
            int rowStart = index;
            int count = 0;

            while (index < n) {
                Cell c = cells.get(index);
                float w = targetHeight * (c.ratioW / c.ratioH);
                float next = rowSum + w;
                float gaps = gap * (count + 2);

                if (count > 0 && next + gaps > width)
                    break;

                rowSum = next;
                count++;
                index++;
            }

            if (count == 0) {
                count = 1;
                Cell c = cells.get(index++);
                rowSum = targetHeight * (c.ratioW / c.ratioH);
            }

            float gaps = gap * (count + 1);
            float targetContent = width - gaps;
            float scale = targetContent / rowSum;

            float rowH = targetHeight * scale;
            float x = gap;

            for (int i = 0; i < count; i++) {
                Cell c = cells.get(rowStart + i);
                float baseW = targetHeight * (c.ratioW / c.ratioH);
                float w = baseW * scale;

                rects.add(new Rectangle2D.Float(x, y, w, rowH));
                x += w + gap;
            }

            y += rowH + gap;
        }

        return rects;
    }

    public static float computeGalleryHeight(
            float targetHeight,
            float gap,
            List<Cell> cells,
            float width
    ) {
        float y = gap;
        int index = 0;
        int n = cells.size();

        while (index < n) {

            float rowSum = 0f;
            int count = 0;

            while (index < n) {
                Cell c = cells.get(index);
                float w = targetHeight * (c.ratioW / c.ratioH);
                float next = rowSum + w;
                float gaps = gap * (count + 2);

                if (count > 0 && next + gaps > width)
                    break;

                rowSum = next;
                count++;
                index++;
            }

            if (count == 0) {
                count = 1;
                Cell c = cells.get(index++);
                rowSum = targetHeight * (c.ratioW / c.ratioH);
            }

            float gaps = gap * (count + 1);
            float targetContent = width - gaps;
            float scale = targetContent / rowSum;

            float rowH = targetHeight * scale;
            y += rowH + gap;
        }

        return y;
    }
}

