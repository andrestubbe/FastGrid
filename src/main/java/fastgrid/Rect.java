package fastgrid;

public final class Rect {
    public float x, y, w, h;

    public Rect(float x, float y, float w, float h) {
        this.x = x; this.y = y; this.w = w; this.h = h;
    }

    public static Rect lerp(Rect a, Rect b, float t) {
        return new Rect(
                a.x + (b.x - a.x) * t,
                a.y + (b.y - a.y) * t,
                a.w + (b.w - a.w) * t,
                a.h + (b.h - a.h) * t
        );
    }
}
