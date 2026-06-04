package fastgrid;

import java.awt.geom.Rectangle2D;

public class Cell {

    // Original ratios
    public final float ratioW;
    public final float ratioH;

    // Precomputed for speed
    public final float aspect;     // ratioW / ratioH
    public final float invAspect;  // ratioH / ratioW

    // Layout rectangles
    public final Rectangle2D.Float outer = new Rectangle2D.Float();
    public final Rectangle2D.Float inner = new Rectangle2D.Float();

    // Optional: index for stable animations / ordering
    public int index;

    // Optional: user data (image path, metadata, etc.)
    public Object data;

    public Cell(float rw, float rh) {
        this.ratioW = rw;
        this.ratioH = rh;

        this.aspect = rw / rh;
        this.invAspect = rh / rw;
    }
}

