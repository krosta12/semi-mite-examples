package com.example.semi_mite_examples.component;

import com.example.semi_mite_examples.dto.Mesh;
import com.example.semi_mite_examples.miteBrich.MeshClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class Example6 implements CommandLineRunner {

    @Autowired
    private MeshClient meshClient;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("\n--- [EXAMPLE 6] Arrays inside Objects: Mesh Scaling ---");

        float[] vertices = {1.0f, 2.0f, 3.0f, 4.0f};
        List<Integer> indices = new ArrayList<>(List.of(0, 1, 2));

        Mesh houseMesh = new Mesh("House3D", vertices, indices);

        System.out.println("Before C++ Vertices: " + java.util.Arrays.toString(houseMesh.vertices));
        System.out.println("Before C++ Indices: " + houseMesh.indices);

        meshClient.scale_mesh_geometry(houseMesh, vertices.length, indices.size(), 2.5f);

        System.out.println("After C++ Vertices (scaled by 2.5): " + java.util.Arrays.toString(houseMesh.vertices));
        System.out.println("After C++ Indices (each +1): " + houseMesh.indices);
    }
}