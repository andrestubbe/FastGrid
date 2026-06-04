package fastgrid;

public final class LayoutMath {

    // HÃ¶he bei gegebener Breite (Masonry-Spalte etc.)
    public static float heightFor(Cell c, float targetWidth, float minSize) {
        float h = targetWidth * (c.ratioH / c.ratioW);
        return Math.max(minSize, h);
    }

    // Breite bei gegebener HÃ¶he (Gallery-Zeile etc.)
    public static float widthFor(Cell c, float targetHeight, float minSize) {
        float w = targetHeight * (c.ratioW / c.ratioH);
        return Math.max(minSize, w);
    }

    public static int minIndex(float[] arr) {
        int best = 0;
        for (int i = 1; i < arr.length; i++)
            if (arr[i] < arr[best]) best = i;
        return best;
    }
}

