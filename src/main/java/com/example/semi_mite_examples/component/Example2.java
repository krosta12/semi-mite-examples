package com.example.semi_mite_examples.component;

import com.example.semi_mite_examples.miteBrich.PhysicsClient;
import org.example.memory.MiteArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Example2 implements CommandLineRunner {

    @Autowired
    private PhysicsClient physicsClient;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("\n--- [EXAMPLE 2] Declarative call via interface client ---");

        int len = 3;
        try (MiteArray x = MiteArray.ofFloats(len);
             MiteArray y = MiteArray.ofFloats(len)) {

            x.setFloat(0, 10f);
            y.setFloat(0, 10f);
            x.setFloat(1, 45f);
            y.setFloat(1, 45f);
            x.setFloat(2, 90f);
            y.setFloat(2, 90f);

            physicsClient.apply_explosion_force(x, y, len, 50.0f, 50.0f, 20.0f, 5.5f);

            System.out.printf("Point 1 after explosion: X=%.2f, Y=%.2f\n", x.getFloat(1), y.getFloat(1));
        }
    }
}