package fastgrid.benchmark;

import fastgrid.*;
import org.openjdk.jmh.annotations.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(1)
public class JMH_Grid {

    private static final int   ITEM_COUNT        = 500;
    private static final int   COLUMNS           = 5;
    private static final float CONTAINER_WIDTH   = 1920f;
    private static final float GAP               = 10f;
    private static final float MIN_SIZE          = 24f;

    private List<Cell> cells;
    private LayoutAlgorithm gridAlgo;
    private LayoutAlgorithm masonryAlgo;
    private LayoutAlgorithm galleryAlgo;

    @Setup(Level.Trial)
    public void setup() {
        float[][] RATIOS = {
                {1, 1}, {4, 3}, {3, 4}, {16, 9}, {9, 16}, {3, 2}, {2, 3}
        };

        Random rand = new Random(42);
        cells = new ArrayList<>(ITEM_COUNT);
        for (int i = 0; i < ITEM_COUNT; i++) {
            float[] r = RATIOS[rand.nextInt(RATIOS.length)];
            cells.add(new Cell(r[0], r[1]));
        }

        gridAlgo    = new GridLayout();
        masonryAlgo = new MasonryLayout();
        galleryAlgo = new GalleryLayout();
    }

    @Benchmark
    public List<Rect> computeGridLayout() {
        LayoutContext ctx = LayoutContext.of(CONTAINER_WIDTH, GAP, MIN_SIZE, COLUMNS);
        LayoutMeasure m   = gridAlgo.measure(cells, ctx);
        return gridAlgo.arrange(cells, ctx, m);
    }

    @Benchmark
    public List<Rect> computeMasonryLayout() {
        LayoutContext ctx = LayoutContext.of(CONTAINER_WIDTH, GAP, MIN_SIZE, COLUMNS);
        LayoutMeasure m   = masonryAlgo.measure(cells, ctx);
        return masonryAlgo.arrange(cells, ctx, m);
    }

    @Benchmark
    public List<Rect> computeGalleryLayout() {
        LayoutContext ctx = LayoutContext.of(CONTAINER_WIDTH, GAP, MIN_SIZE, COLUMNS);
        LayoutMeasure m   = galleryAlgo.measure(cells, ctx);
        return galleryAlgo.arrange(cells, ctx, m);
    }
}
