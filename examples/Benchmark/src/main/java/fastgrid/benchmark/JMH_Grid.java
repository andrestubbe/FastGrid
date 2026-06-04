package fastgrid.benchmark;

import fastgrid.FastGridEngine;
import org.openjdk.jmh.annotations.*;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(1)
public class JMH_Grid {

    private static final int ITEM_COUNT = 100;
    private static final int COLUMNS = 4;
    private static final float CONTAINER_WIDTH = 1920f;
    private static final float GAP = 10f;
    private static final float TARGET_ROW_HEIGHT = 300f;

    private float[] itemAspectRatios;
    private float[] outBounds;
    private float[] scratchColumnHeights;

    @Setup(Level.Trial)
    public void setup() {
        itemAspectRatios = new float[ITEM_COUNT];
        outBounds = new float[ITEM_COUNT * 4];
        scratchColumnHeights = new float[COLUMNS];

        Random rand = new Random(42);
        for (int i = 0; i < ITEM_COUNT; i++) {
            // Random aspect ratios between 0.5 (portrait) and 2.0 (landscape)
            itemAspectRatios[i] = 0.5f + rand.nextFloat() * 1.5f;
        }
    }

    @Benchmark
    public float computeGridLayout() {
        return FastGridEngine.computeGridLayout(
                ITEM_COUNT,
                CONTAINER_WIDTH,
                COLUMNS,
                GAP,
                outBounds
        );
    }

    @Benchmark
    public float computeMasonryLayout() {
        return FastGridEngine.computeMasonryLayout(
                ITEM_COUNT,
                CONTAINER_WIDTH,
                COLUMNS,
                GAP,
                itemAspectRatios,
                scratchColumnHeights,
                outBounds
        );
    }

    @Benchmark
    public float computeGalleryLayout() {
        return FastGridEngine.computeGalleryLayout(
                ITEM_COUNT,
                CONTAINER_WIDTH,
                TARGET_ROW_HEIGHT,
                GAP,
                itemAspectRatios,
                outBounds
        );
    }
}
