package fastgrid;

public final class RatioUtil {

    /**
     * Fit-letterbox: berechnet inner (aspect-correct) in outer.
     * Nutzt c.aspect (vorkalkuliert) statt rw/rh per Frame zu rechnen.
     */
    public static void fit(Rect outer, Rect inner, float aspect) {

        if (outer.w <= 0 || outer.h <= 0) {
            inner.set(outer);
            return;
        }

        float outerRatio = outer.w / outer.h;

        float w, h;
        if (outerRatio > aspect) {
            h = outer.h;
            w = h * aspect;
        } else {
            w = outer.w;
            h = w / aspect;
        }

        inner.x = outer.x + (outer.w - w) * 0.5f;
        inner.y = outer.y + (outer.h - h) * 0.5f;
        inner.w = w;
        inner.h = h;
    }
}
