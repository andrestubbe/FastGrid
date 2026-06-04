package fastgrid;

public record LayoutContext(
        float width,
        float gap,
        float minSize,
        float columns,
        float usableWidth,
        float columnWidth
) {
    public static LayoutContext of(float width, float gap, float minSize, float columns) {
        float cols = Math.max(1f, columns);
        float totalGap = gap * (cols + 1f);
        float usable = Math.max(1f, width - totalGap);
        float colWidth = Math.max(minSize, usable / cols);
        return new LayoutContext(width, gap, minSize, cols, usable, colWidth);
    }
}

