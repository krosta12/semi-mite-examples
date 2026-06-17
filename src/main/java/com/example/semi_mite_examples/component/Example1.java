package com.example.semi_mite_examples.component;

import org.example.engine.CppEngine;
import org.example.memory.MiteArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Example1 implements CommandLineRunner {

    @Autowired
    private CppEngine engine;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("\n--- [EXAMPLE 1] Dynamic call via engine.execute ---");

        int len = 5;
        try (MiteArray vecA = MiteArray.ofFloats(len);
             MiteArray vecB = MiteArray.ofFloats(len)) {

            for (int i = 0; i < len; i++) {
                vecA.setFloat(i, (float) (i + 1)); // [1, 2, 3, 4, 5]
                vecB.setFloat(i, (float) (i + 1)); // [1, 2, 3, 4, 5]
            }

            float similarity = (float) engine.execute("calculate_cosine_similarity", vecA, vecB, len);

            System.out.printf("Result of cosine similarity for identical vectors: %.4f\n", similarity);
        }
    }
}