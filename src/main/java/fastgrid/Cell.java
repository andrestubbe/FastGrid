package fastgrid;

public class Cell {

    // Original ratios
    public final float ratioW;
    public final float ratioH;

    // Precomputed for speed
    public final float aspect;     // ratioW / ratioH

    // Layout rectangles (Primitive float Rects instead of Rectangle2D.Float)
    public final Rect outer = new Rect();
    public final Rect inner = new Rect();

    // Optional: index for stable animations / ordering
    public int index;

    public Cell(float rw, float rh) {
        this.ratioW = rw;
        this.ratioH = rh;
        this.aspect = rw / rh;
    }
}
