package fastgrid;

public final class Rect {
    public float x, y, w, h;

    public Rect() {}

    public Rect(float x, float y, float w, float h) {
        this.x = x; this.y = y; this.w = w; this.h = h;
    }

    public void set(float x, float y, float w, float h) {
        this.x = x; this.y = y; this.w = w; this.h = h;
    }

    public void set(Rect other) {
        this.x = other.x;
        this.y = other.y;
        this.w = other.w;
        this.h = other.h;
    }

    public void setLerp(Rect a, Rect b, float t) {
        this.x = a.x + (b.x - a.x) * t;
        this.y = a.y + (b.y - a.y) * t;
        this.w = a.w + (b.w - a.w) * t;
        this.h = a.h + (b.h - a.h) * t;
    }

    // Optional legacy helper
    public static Rect lerp(Rect a, Rect b, float t) {
        Rect r = new Rect();
        r.setLerp(a, b, t);
        return r;
    }
}
