package ru.javaops.masterjava.matrix;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * gkislin
 * 03.07.2016
 */

/*

My results (4 cores):

Pass 1
Single thread time, sec: 1,076
Concurrent 2 thread time, sec: 0,468
Concurrent maxple thread time, sec: 0,374
Pass 2
Single thread time, sec: 0,890
Concurrent 2 thread time, sec: 0,327
Concurrent maxple thread time, sec: 0,203
Pass 3
Single thread time, sec: 0,889
Concurrent 2 thread time, sec: 0,343
Concurrent maxple thread time, sec: 0,250
Pass 4
Single thread time, sec: 0,858
Concurrent 2 thread time, sec: 0,327
Concurrent maxple thread time, sec: 0,234
Pass 5
Single thread time, sec: 0,861
Concurrent 2 thread time, sec: 0,297
Concurrent maxple thread time, sec: 0,234

Average single thread time, sec: 0,915
Average concurrent 2 thread time, sec: 0,352
Average concurrent maxple thread time, sec: 0,259

*/

public class MainMatrix {
    private static final int MATRIX_SIZE = 1000;
    static final int THREAD_NUMBER = 10;

    private final static ExecutorService executor = Executors.newFixedThreadPool(MainMatrix.THREAD_NUMBER);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        final int[][] matrixA = MatrixUtil.create(MATRIX_SIZE);
        final int[][] matrixB = MatrixUtil.create(MATRIX_SIZE);

        double singleThreadSum = 0.;
        double concurrentThreadSum = 0.;
        double concurrentMaxpleThreadSum = 0.;
        int count = 1;
        while (count < 6) {
            System.out.println("Pass " + count);
            long start = System.currentTimeMillis();
            final int[][] matrixC = MatrixUtil.singleThreadMultiplyOpt(matrixA, matrixB);
            double duration = (System.currentTimeMillis() - start) / 1000.;
            out("Single thread time, sec: %.3f", duration);
            singleThreadSum += duration;

            start = System.currentTimeMillis();
            final int[][] concurrentMatrixC = MatrixUtil.concurrentMultiply2(matrixA, matrixB, executor);
            duration = (System.currentTimeMillis() - start) / 1000.;
            out("Concurrent 2 thread time, sec: %.3f", duration);
            concurrentThreadSum += duration;

            start = System.currentTimeMillis();
            final int[][] concurrentMaxpleMatrixC = MatrixUtil.concurrentMultiplyMaxple(matrixA, matrixB, executor);
            duration = (System.currentTimeMillis() - start) / 1000.;
            out("Concurrent maxple thread time, sec: %.3f", duration);
            concurrentMaxpleThreadSum += duration;

            if (!MatrixUtil.compare(matrixC, concurrentMatrixC) || !MatrixUtil.compare(matrixC, concurrentMaxpleMatrixC)) {
                System.err.println("Comparison failed");
                break;
            }
            count++;
        }
        executor.shutdown();
        out("\nAverage single thread time, sec: %.3f", singleThreadSum / 5.);
        out("Average concurrent 2 thread time, sec: %.3f", concurrentThreadSum / 5.);
        out("Average concurrent maxple thread time, sec: %.3f", concurrentMaxpleThreadSum / 5.);
    }

    private static void out(String format, double ms) {
        System.out.println(String.format(format, ms));
    }
}
