package fastgrid;

class MiniTween {

    private final float start;
    private final float end;
    private float value;
    private final long duration;
    private long startTime;

    private Runnable onUpdate;
    private Runnable onEnd;

    private javax.swing.Timer timer;

    public MiniTween(float start, float end, long duration) {
        this.start = start;
        this.end = end;
        this.duration = duration;
        this.value = start;
    }

    public MiniTween onUpdate(Runnable r) { this.onUpdate = r; return this; }
    public MiniTween onEnd(Runnable r) { this.onEnd = r; return this; }

    public void start() {
        startTime = System.currentTimeMillis();

        timer = new javax.swing.Timer(16, e -> {
            float t = (System.currentTimeMillis() - startTime) / (float) duration;
            if (t >= 1f) t = 1f;

            t = easeOutCubic(t);
            value = start + (end - start) * t;

            if (onUpdate != null) onUpdate.run();

            if (t >= 1f) {
                timer.stop();
                if (onEnd != null) onEnd.run();
            }
        });

        timer.start();
    }

    private float easeOutCubic(float t) {
        t -= 1f;
        return t * t * t + 1f;
    }

    public float getValue() { return value; }
}
