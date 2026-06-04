package fastproportion.benchmark;

import fastproportion.Proportion;
import fastproportion.ProportionMode;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 10, time = 1)
@Fork(3)
@State(Scope.Thread)
public class JMH_Proportion {

    private Proportion proportion;
    private final float[] out = new float[4];

    @Setup(Level.Iteration)
    public void setup() {
        // Create it once per thread to avoid allocation overhead during measurement
        proportion = new Proportion(500, 500, 1920, 1080);
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public float computeContainZeroAllocation() {
        // Measures the zero-allocation throughput for CONTAIN mode
        proportion.compute(ProportionMode.CONTAIN, out);
        return out[0]; // force JIT to keep the computation
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public float computeCoverZeroAllocation() {
        // Measures the zero-allocation throughput for COVER mode
        proportion.compute(ProportionMode.COVER, out);
        return out[0]; // force JIT to keep the computation
    }
}
