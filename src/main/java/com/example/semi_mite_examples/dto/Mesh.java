package com.example.semi_mite_examples.dto;

import java.util.ArrayList;
import java.util.List;

public class Mesh {
    public String name;
    public float[] vertices; 
    public List<Integer> indices = new ArrayList<>();

    public Mesh(String name, float[] vertices, List<Integer> indices) {
        this.name = name;
        this.vertices = vertices;
        this.indices = indices;
    }
}