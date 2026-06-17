package com.example.semi_mite_examples.component;

import com.example.semi_mite_examples.miteBrich.PhysicsClient;
import com.example.semi_mite_examples.objects.Particle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Example3 implements CommandLineRunner {

    @Autowired
    private PhysicsClient physicsClient;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("\n--- [EXAMPLE 3] Transfer of complex Java objects to C++ ---");

        Particle myParticle = new Particle(777, 0.0f, 0.0f, 12.5f);
        System.out.println("Before transfer to C++: " + myParticle);

        physicsClient.process_particle(myParticle);

        System.out.println("After processing in C++ (values have changed!): " + myParticle);
    }
}

