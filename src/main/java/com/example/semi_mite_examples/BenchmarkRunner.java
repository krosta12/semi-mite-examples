package com.example.semi_mite_examples;

import org.example.engine.CppEngine;
import org.example.memory.MiteArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Random;

@Component
public class BenchmarkRunner implements CommandLineRunner {

    @Autowired
    private CppEngine engine;

    @Autowired
    private PhysicsService physicsService;

    @Override
    public void run(String... args) throws Exception {
        int totalObjects = 20_000_000;
        float targetX = 50.0f;
        float targetY = 50.0f;
        float radius = 20.0f;
        float force = 5.5f;

        System.out.println("=== Generating data ===");
        float[] origX = new float[totalObjects];
        float[] origY = new float[totalObjects];
        Random random = new Random(42);

        for (int i = 0; i < totalObjects; i++) {
            origX[i] = random.nextFloat() * 100.0f;
            origY[i] = random.nextFloat() * 100.0f;
        }
        System.out.println("Number of objects: " + totalObjects);

        float[] javaX = Arrays.copyOf(origX, totalObjects);
        float[] javaY = Arrays.copyOf(origY, totalObjects);

        try (MiteArray cppX = MiteArray.ofFloats(totalObjects);
             MiteArray cppY = MiteArray.ofFloats(totalObjects)) {

            System.out.println("\n=== Warmup (50 iterations) ===");
            for (int i = 0; i < 50; i++) {
                System.arraycopy(origX, 0, javaX, 0, totalObjects);
                System.arraycopy(origY, 0, javaY, 0, totalObjects);
                physicsService.runJavaPhysics(javaX, javaY, targetX, targetY, radius, force);

                for (int j = 0; j < totalObjects; j++) {
                    cppX.setFloat(j, origX[j]);
                    cppY.setFloat(j, origY[j]);
                }
                engine.execute("apply_explosion_force", cppX, cppY, totalObjects, targetX, targetY, radius, force);
            }
            System.out.println("Warmup completed successfully.");

            int iterations = 30;

            System.out.println("\n=== STARTING JAVA BENCHMARK ===");
            double totalJavaTimeMs = 0;

            for (int r = 0; r < iterations; r++) {
                System.arraycopy(origX, 0, javaX, 0, totalObjects);
                System.arraycopy(origY, 0, javaY, 0, totalObjects);

                long javaStart = System.nanoTime();
                physicsService.runJavaPhysics(javaX, javaY, targetX, targetY, radius, force);
                long javaEnd = System.nanoTime();

                totalJavaTimeMs += (javaEnd - javaStart) / 1_000_000.0;
            }
            double avgJavaDurationMs = totalJavaTimeMs / iterations;
            System.out.printf("Clean Java (average over %d runs): %.3f ms\n", iterations, avgJavaDurationMs);

            System.out.println("\n=== STARTING C++ BENCHMARK (semi-mite Zero-Copy) ===");
            double totalCppTimeMs = 0;

            for (int r = 0; r < iterations; r++) {

                for (int j = 0; j < totalObjects; j++) {
                    cppX.setFloat(j, origX[j]);
                    cppY.setFloat(j, origY[j]);
                }

                long cppStart = System.nanoTime();
                engine.execute("apply_explosion_force", cppX, cppY, totalObjects, targetX, targetY, radius, force);
                long cppEnd = System.nanoTime();

                totalCppTimeMs += (cppEnd - cppStart) / 1_000_000.0;
            }
            double avgCppDurationMs = totalCppTimeMs / iterations;
            System.out.printf("C++ via Zero-Copy Off-Heap (average over %d runs): %.3f ms\n", iterations, avgCppDurationMs);

            boolean isValid = true;
            for (int i = 0; i < 1000; i++) {
                if (Math.abs(javaX[i] - cppX.getFloat(i)) > 0.001f || Math.abs(javaY[i] - cppY.getFloat(i)) > 0.001f) {
                    isValid = false;
                    break;
                }
            }

            System.out.println("\n=== BENCHMARK RESULTS ===");
            if (isValid) {
                System.out.println(" Results for Java and C++ are identical!");
                double speedup = avgJavaDurationMs / avgCppDurationMs;
                if (speedup > 1.0) {
                    System.out.printf("Ultimate speedup due to C++: %.2fx faster\n", speedup);
                } else {
                    System.out.printf("Java is faster by %.2fx (C++ is slower)\n", 1.0 / speedup);
                }
            } else {
                System.out.println(" Error: Calculations for Java and C++ differ! Check offsets in the marshaller.");
            }

        }
    }
}