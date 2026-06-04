package fastgrid;

import java.awt.geom.Rectangle2D;

public class RatioUtil {

    public static void fit(Rectangle2D.Float outer, Rectangle2D.Float inner, float rw, float rh) {

        if (outer.width <= 0 || outer.height <= 0) {
            inner.setRect(outer);
            return;
        }

        float target = rw / rh;
        float outerRatio = outer.width / outer.height;

        float w, h;

        if (outerRatio > target) {
            h = outer.height;
            w = h * target;
        } else {
            w = outer.width;
            h = w / target;
        }

        float x = outer.x + (outer.width - w) * 0.5f;
        float y = outer.y + (outer.height - h) * 0.5f;

        inner.setRect(x, y, w, h);
    }
}

