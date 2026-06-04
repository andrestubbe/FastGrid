package fastgrid;

/**
 * High-performance, zero-allocation multi-item layout engine.
 * Computes complex Grid, Masonry, and Gallery layouts completely in a pure 32-bit float pipeline.
 *
 * Output bounds are always written into a flat 1D float array:
 * [x0, y0, w0, h0, x1, y1, w1, h1, ...]
 */
public final class FastGridEngine {

    private FastGridEngine() {}

    /**
     * Computes a strict square Grid layout.
     *
     * @param itemCount      The number of items.
     * @param containerWidth The available width of the container.
     * @param columns        The number of columns.
     * @param gap            The gap between items.
     * @param outBounds      The output buffer for bounds (length must be at least itemCount * 4).
     * @return The total height of the layout.
     */
    public static float computeGridLayout(
            int itemCount,
            float containerWidth,
            int columns,
            float gap,
            float[] outBounds) {

        if (itemCount == 0) return gap;

        float colWidth = (containerWidth - gap * (columns + 1)) / columns;
        float x = gap;
        float y = gap;

        for (int i = 0; i < itemCount; i++) {
            int outIdx = i * 4;
            outBounds[outIdx] = x;
            outBounds[outIdx + 1] = y;
            outBounds[outIdx + 2] = colWidth;
            outBounds[outIdx + 3] = colWidth; // Square grid

            x += colWidth + gap;
            if (x + colWidth > containerWidth + 0.5f) {
                x = gap;
                y += colWidth + gap;
            }
        }

        float rows = (float) Math.ceil((double) itemCount / columns);
        return rows * (colWidth + gap) + gap;
    }

    /**
     * Computes a Masonry (Pinterest-style) layout.
     *
     * @param itemCount            The number of items.
     * @param containerWidth       The available width of the container.
     * @param columns              The number of columns.
     * @param gap                  The gap between items.
     * @param itemAspectRatios     Input array of aspect ratios (width/height) for each item.
     * @param scratchColumnHeights A scratch buffer (length = columns) to track column heights. 
     *                             Passed externally to guarantee strict zero-allocation.
     * @param outBounds            The output buffer for bounds (length must be at least itemCount * 4).
     * @return The total height of the layout.
     */
    public static float computeMasonryLayout(
            int itemCount,
            float containerWidth,
            int columns,
            float gap,
            float[] itemAspectRatios,
            float[] scratchColumnHeights,
            float[] outBounds) {

        if (itemCount == 0) return gap;

        float colWidth = (containerWidth - gap * (columns + 1)) / columns;

        for (int i = 0; i < columns; i++) {
            scratchColumnHeights[i] = gap;
        }

        for (int i = 0; i < itemCount; i++) {
            int minCol = 0;
            float minH = scratchColumnHeights[0];
            for (int c = 1; c < columns; c++) {
                if (scratchColumnHeights[c] < minH) {
                    minH = scratchColumnHeights[c];
                    minCol = c;
                }
            }

            float aspect = itemAspectRatios[i];
            float h = colWidth / aspect;

            int outIdx = i * 4;
            outBounds[outIdx] = gap + minCol * (colWidth + gap);
            outBounds[outIdx + 1] = minH;
            outBounds[outIdx + 2] = colWidth;
            outBounds[outIdx + 3] = h;

            scratchColumnHeights[minCol] += h + gap;
        }

        float maxH = scratchColumnHeights[0];
        for (int c = 1; c < columns; c++) {
            if (scratchColumnHeights[c] > maxH) {
                maxH = scratchColumnHeights[c];
            }
        }

        return maxH;
    }

    /**
     * Computes a Gallery (Flickr-style justified) layout.
     * Elements are scaled proportionally to fill rows perfectly.
     *
     * @param itemCount        The number of items.
     * @param containerWidth   The available width of the container.
     * @param targetRowHeight  The ideal height for a row before scaling.
     * @param gap              The gap between items.
     * @param itemAspectRatios Input array of aspect ratios (width/height) for each item.
     * @param outBounds        The output buffer for bounds (length must be at least itemCount * 4).
     * @return The total height of the layout.
     */
    public static float computeGalleryLayout(
            int itemCount,
            float containerWidth,
            float targetRowHeight,
            float gap,
            float[] itemAspectRatios,
            float[] outBounds) {

        if (itemCount == 0) return gap;

        float y = gap;
        int index = 0;

        while (index < itemCount) {
            float rowSum = 0f;
            int rowStart = index;
            int count = 0;

            while (index < itemCount) {
                float w = targetRowHeight * itemAspectRatios[index];
                float next = rowSum + w;
                float gaps = gap * (count + 2);

                if (count > 0 && next + gaps > containerWidth) {
                    break;
                }

                rowSum = next;
                count++;
                index++;
            }

            if (count == 0) {
                count = 1;
                rowSum = targetRowHeight * itemAspectRatios[index];
                index++;
            }

            float gaps = gap * (count + 1);
            float targetContent = containerWidth - gaps;
            float scale = targetContent / rowSum;

            float rowH = targetRowHeight * scale;
            float x = gap;

            for (int i = 0; i < count; i++) {
                int itemIndex = rowStart + i;
                float baseW = targetRowHeight * itemAspectRatios[itemIndex];
                float w = baseW * scale;

                int outIdx = itemIndex * 4;
                outBounds[outIdx] = x;
                outBounds[outIdx + 1] = y;
                outBounds[outIdx + 2] = w;
                outBounds[outIdx + 3] = rowH;

                x += w + gap;
            }

            y += rowH + gap;
        }

        return y;
    }
}
