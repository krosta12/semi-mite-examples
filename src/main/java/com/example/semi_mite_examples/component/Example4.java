package com.example.semi_mite_examples.component;

import org.example.engine.CppEngine;
import org.example.memory.MiteArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Example4 implements CommandLineRunner {

    @Autowired
    private CppEngine engine;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("\n--- [EXAMPLE 4] Types of data transferring: Copying vs Zero-Copy Bridge ---");

        int size = 10_000_000;
        float[] standardJavaArrayA = new float[size];
        float[] standardJavaArrayB = new float[size];

        long startCopy = System.nanoTime();

        engine.execute("calculate_cosine_similarity", standardJavaArrayA, standardJavaArrayB, size);

        long endCopy = System.nanoTime();
        System.out.printf("Time of call with implicit data copying: %.2f ms\n", (endCopy - startCopy) / 1_000_000.0);


        try (MiteArray bridgeArrayA = MiteArray.ofFloats(size);
             MiteArray bridgeArrayB = MiteArray.ofFloats(size)) {

            long startZeroCopy = System.nanoTime();

            engine.execute("calculate_cosine_similarity", bridgeArrayA, bridgeArrayB, size);

            long endZeroCopy = System.nanoTime();
            System.out.printf("Time of call through our Zero-Copy bridge (MiteArray): %.2f ms\n", (endZeroCopy - startZeroCopy) / 1_000_000.0);
            System.out.println("EXPLANATION: Our bridge makes the call instant, eliminating the load on the Garbage Collector and memory bus!");
        }
    }
}