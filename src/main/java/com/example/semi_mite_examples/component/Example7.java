package com.example.semi_mite_examples.component;

import com.example.semi_mite_examples.dto.User;
import com.example.semi_mite_examples.miteBrich.GraphClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Example7 implements CommandLineRunner {

    @Autowired
    private GraphClient graphClient;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("\n--- [EXAMPLE 7] Cyclic Graphs: Social Network ---");

        User alex = new User("Alex", 25);
        User bob = new User("Bob", 30);

        alex.friends.add(bob);
        bob.friends.add(alex);

        System.out.println("Before C++ -> Alex age: " + alex.age + ", Bob age: " + bob.age);

        graphClient.celebrate_birthday_system(alex);

        System.out.println("After C++ -> Alex age (25 + 1): " + alex.age);
        System.out.println("After C++ -> Bob age (30 + 5): " + bob.age);
    }
}