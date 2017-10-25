package ru.javaops.masterjava.matrix;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/*
# Run complete. Total time: 00:10:21

Benchmark                                  (threadNumber)  Mode  Cnt    Score    Error  Units
MatrixBenchmark.concurrentMultiply2                     3    ss  100  324,787 ?  8,682  ms/op
MatrixBenchmark.concurrentMultiply2                     4    ss  100  299,190 ? 20,809  ms/op
MatrixBenchmark.concurrentMultiply2                    10    ss  100  298,859 ? 12,887  ms/op
MatrixBenchmark.concurrentMultiplyMaxple                3    ss  100  477,705 ?  6,604  ms/op
MatrixBenchmark.concurrentMultiplyMaxple                4    ss  100  350,472 ?  4,765  ms/op
MatrixBenchmark.concurrentMultiplyMaxple               10    ss  100  249,847 ?  7,254  ms/op
MatrixBenchmark.concurrentMultiplyStreams               3    ss  100  341,904 ? 10,199  ms/op
MatrixBenchmark.concurrentMultiplyStreams               4    ss  100  278,431 ?  5,094  ms/op
MatrixBenchmark.concurrentMultiplyStreams              10    ss  100  284,124 ?  5,026  ms/op
 */

@Warmup(iterations = 10)
@Measurement(iterations = 10)
@BenchmarkMode({Mode.SingleShotTime})
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Threads(1)
@Fork(10)
@Timeout(time = 5, timeUnit = TimeUnit.MINUTES)
public class MatrixBenchmark {

    // Matrix size
    private static final int MATRIX_SIZE = 1000;

    @Param({"3", "4", "10"})
    private int threadNumber;

    private static int[][] matrixA;
    private static int[][] matrixB;

    @Setup
    public void setUp() {
        matrixA = MatrixUtil.create(MATRIX_SIZE);
        matrixB = MatrixUtil.create(MATRIX_SIZE);
    }

    private ExecutorService executor;

    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
                .include(MatrixBenchmark.class.getSimpleName())
                .threads(1)
                .forks(10)
                .timeout(TimeValue.minutes(5))
                .build();
        new Runner(options).run();
    }

    //    @Benchmark
    public int[][] singleThreadMultiplyOpt() throws Exception {
        return MatrixUtil.singleThreadMultiplyOpt(matrixA, matrixB);
    }

    //    @Benchmark
    public int[][] singleThreadMultiplyOpt2() throws Exception {
        return MatrixUtil.singleThreadMultiplyOpt(matrixA, matrixB);
    }

    @Benchmark
    public int[][] concurrentMultiplyStreams() throws Exception {
        return MatrixUtil.concurrentMultiplyStreams(matrixA, matrixB, threadNumber);
    }

    //    @Benchmark
    public int[][] concurrentMultiply() throws Exception {
        return MatrixUtil.concurrentMultiply(matrixA, matrixB, executor);
    }

    @Benchmark
    public int[][] concurrentMultiply2() throws Exception {
        return MatrixUtil.concurrentMultiply2(matrixA, matrixB, executor);
    }

    @Benchmark
    public int[][] concurrentMultiplyMaxple() throws Exception {
        return MatrixUtil.concurrentMultiplyMaxple(matrixA, matrixB, executor, threadNumber);
    }

    @Setup
    public void setup() {
        executor = Executors.newFixedThreadPool(threadNumber);
    }

    @TearDown
    public void tearDown() {
        executor.shutdown();
    }
}