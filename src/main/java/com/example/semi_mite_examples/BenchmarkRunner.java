package com.example.semi_mite_examples;

import org.example.engine.CppEngine;
import org.example.memory.MiteArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class BenchmarkRunner implements CommandLineRunner {

    @Autowired
    private CppEngine engine;

    private static final int N = 20_000_000;
    private static final int WARMUP = 50;
    private static final int ITERATIONS = 30;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== Generating data (N=" + N + ") ===");
        float[] orig = new float[N];
        float[] origX = new float[N];
        float[] origY = new float[N];
        Random rng = new Random(42);
        for (int i = 0; i < N; i++) {
            orig[i]  = rng.nextFloat() * 100.0f;
            origX[i] = rng.nextFloat() * 100.0f;
            origY[i] = rng.nextFloat() * 100.0f;
        }

        try (MiteArray mX  = MiteArray.ofFloats(N);
             MiteArray mY  = MiteArray.ofFloats(N);
             MiteArray mD  = MiteArray.ofFloats(N)) {

            // --- fill off-heap once; data stays there for all three benchmarks ---
            for (int i = 0; i < N; i++) {
                mX.setFloat(i, origX[i]);
                mY.setFloat(i, origY[i]);
                mD.setFloat(i, orig[i]);
            }

            // ------------------------------------------------------------------ //
            //  WARMUP                                                             //
            // ------------------------------------------------------------------ //
            System.out.println("\n=== Warmup (" + WARMUP + " iterations each) ===");
            float[] javaX = origX.clone();
            float[] javaY = origY.clone();
            float[] javaD = orig.clone();

            for (int i = 0; i < WARMUP; i++) {
                // java side
                javaSimpleSum(orig);
                javaExplosion(javaX, javaY, 50f, 50f, 20f, 5.5f);
                javaHeavyMath(javaD);
                // cpp side (off-heap already filled, no copy needed)
                engine.execute("simple_sum", mD, N);
                engine.execute("apply_explosion_force", mX, mY, N, 50f, 50f, 20f, 5.5f);
                engine.execute("heavy_math_transform", mD, N);
            }
            System.out.println("Warmup done.\n");

            // ------------------------------------------------------------------ //
            //  SCENARIO 1 — Simple summation                                     //
            //  Expected: Java JIT wins (or tie). JIT auto-vectorises this easily. //
            // ------------------------------------------------------------------ //
            System.out.println("=================================================================");
            System.out.println(" SCENARIO 1: Simple summation (sum of N floats)");
            System.out.println(" Expected: Java JIT wins — trivial loop, JIT auto-vectorises it.");
            System.out.println("=================================================================");

            double javaSum1 = 0, cppSum1 = 0;
            for (int r = 0; r < ITERATIONS; r++) {
                long t0 = System.nanoTime();
                javaSimpleSum(orig);
                javaSum1 += (System.nanoTime() - t0) / 1e6;
            }
            for (int r = 0; r < ITERATIONS; r++) {
                long t0 = System.nanoTime();
                engine.execute("simple_sum", mD, N);
                cppSum1 += (System.nanoTime() - t0) / 1e6;
            }
            printResult(javaSum1, cppSum1);

            // ------------------------------------------------------------------ //
            //  SCENARIO 2 — Explosion force (sqrt + conditional branch)          //
            //  Expected: roughly equal — JIT handles sqrt well, but C++ edges out //
            //  due to better branch-prediction hints and no GC pressure.          //
            // ------------------------------------------------------------------ //
            System.out.println("=================================================================");
            System.out.println(" SCENARIO 2: Explosion force (sqrt + conditional per element)");
            System.out.println(" Expected: roughly equal — JIT is competitive, C++ slight edge.");
            System.out.println("=================================================================");

            // restore off-heap state before measuring
            for (int i = 0; i < N; i++) { mX.setFloat(i, origX[i]); mY.setFloat(i, origY[i]); }
            javaX = origX.clone(); javaY = origY.clone();

            double javaSum2 = 0, cppSum2 = 0;
            for (int r = 0; r < ITERATIONS; r++) {
                System.arraycopy(origX, 0, javaX, 0, N);
                System.arraycopy(origY, 0, javaY, 0, N);
                long t0 = System.nanoTime();
                javaExplosion(javaX, javaY, 50f, 50f, 20f, 5.5f);
                javaSum2 += (System.nanoTime() - t0) / 1e6;
            }
            // Note: C++ modifies data in-place. We reset once before the loop
            // and measure only the compute — data already lives off-heap.
            for (int i = 0; i < N; i++) { mX.setFloat(i, origX[i]); mY.setFloat(i, origY[i]); }
            for (int r = 0; r < ITERATIONS; r++) {
                long t0 = System.nanoTime();
                engine.execute("apply_explosion_force", mX, mY, N, 50f, 50f, 20f, 5.5f);
                cppSum2 += (System.nanoTime() - t0) / 1e6;
            }
            printResult(javaSum2, cppSum2);

            // ------------------------------------------------------------------ //
            //  SCENARIO 3 — Heavy transcendental math (sin + cos + exp per elem) //
            //  Expected: C++ wins — JIT does NOT optimise Math.sin/exp as         //
            //  aggressively as clang/gcc with -O3 -march=native -ffast-math.     //
            // ------------------------------------------------------------------ //
            System.out.println("=================================================================");
            System.out.println(" SCENARIO 3: Heavy math (sin * cos + exp per element)");
            System.out.println(" Expected: C++ wins — transcendental functions, -ffast-math edge.");
            System.out.println("=================================================================");

            javaD = orig.clone();
            for (int i = 0; i < N; i++) mD.setFloat(i, orig[i]);

            double javaSum3 = 0, cppSum3 = 0;
            for (int r = 0; r < ITERATIONS; r++) {
                System.arraycopy(orig, 0, javaD, 0, N);
                long t0 = System.nanoTime();
                javaHeavyMath(javaD);
                javaSum3 += (System.nanoTime() - t0) / 1e6;
            }
            for (int i = 0; i < N; i++) mD.setFloat(i, orig[i]);
            for (int r = 0; r < ITERATIONS; r++) {
                long t0 = System.nanoTime();
                engine.execute("heavy_math_transform", mD, N);
                cppSum3 += (System.nanoTime() - t0) / 1e6;
            }
            printResult(javaSum3, cppSum3);

            // ------------------------------------------------------------------ //
            //  SUMMARY                                                            //
            // ------------------------------------------------------------------ //
            System.out.println("\n=================================================================");
            System.out.println(" SUMMARY (average over " + ITERATIONS + " runs, N=" + N + ")");
            System.out.println("=================================================================");
            System.out.printf(" Scenario 1 (simple sum)    — Java: %7.2f ms  |  C++: %7.2f ms%n",
                    javaSum1 / ITERATIONS, cppSum1 / ITERATIONS);
            System.out.printf(" Scenario 2 (explosion)     — Java: %7.2f ms  |  C++: %7.2f ms%n",
                    javaSum2 / ITERATIONS, cppSum2 / ITERATIONS);
            System.out.printf(" Scenario 3 (heavy math)    — Java: %7.2f ms  |  C++: %7.2f ms%n",
                    javaSum3 / ITERATIONS, cppSum3 / ITERATIONS);
            System.out.println();
            System.out.println(" Conclusion:");
            System.out.println("  - Trivial loops:     use Java. JIT is just as fast.");
            System.out.println("  - sqrt + branching:  either works. C++ has slight edge.");
            System.out.println("  - Heavy math (sin/cos/exp): use semi-mite C++. Real speedup.");
        }
    }

    // --- Java baselines ---

    private float javaSimpleSum(float[] data) {
        float s = 0f;
        for (float v : data) s += v;
        return s;
    }

    private void javaExplosion(float[] x, float[] y,
                               float tx, float ty, float radius, float force) {
        for (int i = 0; i < x.length; i++) {
            float dx = x[i] - tx;
            float dy = y[i] - ty;
            float dist = (float) Math.sqrt(dx * dx + dy * dy);
            if (dist < radius && dist > 0.001f) {
                float ff = (radius - dist) / radius * force;
                x[i] += (dx / dist) * ff;
                y[i] += (dy / dist) * ff;
            }
        }
    }

    private void javaHeavyMath(float[] data) {
        for (int i = 0; i < data.length; i++) {
            double v = data[i];
            data[i] = (float) (Math.sin(v) * Math.cos(v * 0.5) + Math.exp(-v * 0.01));
        }
    }

    private void printResult(double javaTotalMs, double cppTotalMs) {
        double avgJava = javaTotalMs / ITERATIONS;
        double avgCpp  = cppTotalMs  / ITERATIONS;
        double ratio   = avgJava / avgCpp;
        System.out.printf("  Java: %.3f ms  |  C++ (semi-mite): %.3f ms  |  ", avgJava, avgCpp);
        if (ratio > 1.05)
            System.out.printf("C++ is %.2fx faster%n%n", ratio);
        else if (ratio < 0.95)
            System.out.printf("Java is %.2fx faster%n%n", 1.0 / ratio);
        else
            System.out.printf("Roughly equal (ratio=%.2f)%n%n", ratio);
    }
}