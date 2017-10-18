package ru.javaops.masterjava.matrix;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.System.currentTimeMillis;

/**
 * gkislin
 * 03.07.2016
 */
public class MainMatrix {
    private static final int MATRIX_SIZE = 1000;
    private static final int THREAD_NUMBER = 10;

    private final static ExecutorService executor = Executors.newFixedThreadPool(MainMatrix.THREAD_NUMBER);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        final int[][] matrixA = MatrixUtil.create(MATRIX_SIZE);
        final int[][] matrixB = MatrixUtil.create(MATRIX_SIZE);

        final int optPageSize = new MainMatrix().getOptPageSize(matrixA, matrixB);
        System.out.println("optPageSize=" + optPageSize);

        double singleThreadSum = 0.;
        double singleThreadOptimizedSum = 0.;
        double concurrentThreadSum = 0.;
        int count = 0;
        while (count < 5) {
            count++;
            System.out.println("Pass " + count);
            long start = currentTimeMillis();
            final int[][] matrixC = MatrixUtil.singleThreadMultiply(matrixA, matrixB);
            double duration = (currentTimeMillis() - start) / 1000.;
            out("Single thread time, sec: %.3f", duration);
            singleThreadSum += duration;

            start = currentTimeMillis();
            final int[][] singleThreadOptimizedMatrixC = MatrixUtil.singleThreadOptimizedMultiply(matrixA, matrixB);
            duration = (currentTimeMillis() - start) / 1000.;
            out("Single thread optimized time, sec: %.3f", duration);
            singleThreadOptimizedSum += duration;

            start = currentTimeMillis();
            final int[][] concurrentMatrixC = MatrixUtil.concurrentMultiply(matrixA, matrixB, executor, optPageSize);
            duration = (currentTimeMillis() - start) / 1000.;
            out("Concurrent thread time, sec: %.3f", duration);
            concurrentThreadSum += duration;

            if (!MatrixUtil.compare(matrixC, singleThreadOptimizedMatrixC)) {
                System.err.println("Comparison 1 failed");
                break;
            }

            if (!MatrixUtil.compare(matrixC, concurrentMatrixC)) {
                System.err.println("Comparison 2 failed");
                break;
            }
        }
        executor.shutdown();
        out("\nAverage single thread time, sec: %.3f", singleThreadSum / count);
        out("Average single thread optimized time, sec: %.3f", singleThreadOptimizedSum / count);
        out("Average concurrent thread time, sec: %.3f", concurrentThreadSum / count);
    }

    private int getOptPageSize(int[][] matrixA, int[][] matrixB) throws ExecutionException, InterruptedException {
        double minDuration = Double.MAX_VALUE;

        List<Info> infoList = new ArrayList<>();

        for (int ps = 1; ps <= 100; ps++) {
            System.out.println("Page size = " + ps);
            long start = currentTimeMillis();
            final int[][] matrixC = MatrixUtil.concurrentMultiply(matrixA, matrixB, executor, ps);
            double duration = (currentTimeMillis() - start) / 1000.;
            minDuration = Math.min(minDuration, duration);
            //out("duration: %.3f", duration);
            //out("minDuration: %.3f", minDuration);
            infoList.add(new Info(duration, ps));
        }

        Collections.sort(infoList);

        for (Info info : infoList) {
            System.out.println(info);
        }

        return infoList.get(0).pageSize;
    }

    private static void out(String format, double ms) {
        System.out.println(String.format(format, ms));
    }

    private class Info implements Comparable<Info> {
        private double duration;
        private int pageSize;

        public Info(double duration, int pageSize) {
            this.duration = duration;
            this.pageSize = pageSize;
        }

        @Override
        public int compareTo(Info o) {
            return Double.compare(duration, o.duration);
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Info{");
            sb.append("duration=").append(duration);
            sb.append(", pageSize=").append(pageSize);
            sb.append('}');
            return sb.toString();
        }
    }
}