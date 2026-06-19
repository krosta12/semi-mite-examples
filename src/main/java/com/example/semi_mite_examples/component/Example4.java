package com.example.semi_mite_examples.component;

import org.example.engine.CppEngine;
import org.example.memory.MiteArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * EXAMPLE 4 — Repeated native calls: heap array vs MiteArray
 *
 * Scenario: a native function is called many times on the same data
 * (e.g. physics tick, signal processing, simulation step).
 *
 * With a plain Java float[]:
 *   Every call marshals the entire array from JVM heap to off-heap memory.
 *   That copy happens on EVERY call, regardless of whether the data changed.
 *
 * With MiteArray:
 *   Memory is allocated off-heap once.
 *   C++ reads and writes it directly.
 *   Java and C++ share the same memory region — no copy per call.
 *
 * This benchmark measures total wall time for CALLS_COUNT repeated invocations,
 * including all marshalling overhead, so the comparison is fair and complete.
 */
@Component
public class Example4 implements CommandLineRunner {

    @Autowired
    private CppEngine engine;

    private static final int SIZE        = 5_000_000;
    private static final int CALLS_COUNT = 20;
    private static final int WARMUP      = 10;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("\n--- [EXAMPLE 4] Repeated Native Calls: Heap Array vs Zero-Copy MiteArray ---");

        float[] javaArrayA = new float[SIZE];
        float[] javaArrayB = new float[SIZE];
        Random rng = new Random(42);
        for (int i = 0; i < SIZE; i++) {
            javaArrayA[i] = rng.nextFloat();
            javaArrayB[i] = rng.nextFloat();
        }

        try (MiteArray miteA = MiteArray.ofFloats(SIZE);
             MiteArray miteB = MiteArray.ofFloats(SIZE)) {

            for (int i = 0; i < SIZE; i++) {
                miteA.setFloat(i, javaArrayA[i]);
                miteB.setFloat(i, javaArrayB[i]);
            }

            for (int i = 0; i < WARMUP; i++) {
                engine.execute("calculate_cosine_similarity", javaArrayA, javaArrayB, SIZE);
                engine.execute("calculate_cosine_similarity", miteA, miteB, SIZE);
            }

            long startHeap = System.nanoTime();
            for (int i = 0; i < CALLS_COUNT; i++) {
                engine.execute("calculate_cosine_similarity", javaArrayA, javaArrayB, SIZE);
            }
            long endHeap = System.nanoTime();
            double totalHeapMs  = (endHeap - startHeap) / 1_000_000.0;
            double avgHeapMs    = totalHeapMs / CALLS_COUNT;

            long startMite = System.nanoTime();
            for (int i = 0; i < CALLS_COUNT; i++) {
                engine.execute("calculate_cosine_similarity", miteA, miteB, SIZE);
            }
            long endMite = System.nanoTime();
            double totalMiteMs  = (endMite - startMite) / 1_000_000.0;
            double avgMiteMs    = totalMiteMs / CALLS_COUNT;

            System.out.printf("%nDataset size : %,d floats per array%n", SIZE);
            System.out.printf("Repeated calls: %d%n%n", CALLS_COUNT);

            System.out.printf("Heap array  (marshal every call) — total: %6.1f ms  |  avg per call: %.2f ms%n",
                    totalHeapMs, avgHeapMs);
            System.out.printf("MiteArray   (zero-copy off-heap) — total: %6.1f ms  |  avg per call: %.2f ms%n",
                    totalMiteMs, avgMiteMs);

            double ratio = avgHeapMs / avgMiteMs;
            System.out.printf("%nMiteArray is %.2fx faster per call%n", ratio);
            System.out.printf("Over %d calls, total time saved: %.1f ms%n",
                    CALLS_COUNT, totalHeapMs - totalMiteMs);
        }
    }
}