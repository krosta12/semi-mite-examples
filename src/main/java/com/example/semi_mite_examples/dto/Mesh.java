package com.example.semi_mite_examples.dto;

import org.example.annotation.MiteStruct;

import java.util.ArrayList;
import java.util.List;

@MiteStruct
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