package com.example.semi_mite_examples.component;

import com.example.semi_mite_examples.dto.Node;
import com.example.semi_mite_examples.miteBrich.TreeClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Example5 implements CommandLineRunner {

    @Autowired
    private TreeClient treeClient;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("\n--- [EXAMPLE 5] Deep Objects: Binary Tree Inversion ---");

        Node root = new Node(1);
        root.left = new Node(2);
        root.right = new Node(3);

        System.out.println("Before C++: " + root);
        System.out.println("Before C++ (Left): " + root.left);
        System.out.println("Before C++ (Right): " + root.right);

        treeClient.invert_and_increment_tree(root);

        System.out.println("After C++ (Root modified): " + root);
        System.out.println("After C++ (Left is now 3 + 100): " + root.left);
        System.out.println("After C++ (Right is now 2 + 100): " + root.right);
    }
}