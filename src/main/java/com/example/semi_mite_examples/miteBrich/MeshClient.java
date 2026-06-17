package com.example.semi_mite_examples.miteBrich;

import com.example.semi_mite_examples.dto.Mesh;
import org.example.client.MiteClient;

@MiteClient(script = "cppScripts/mesh_processor.cpp")
public interface MeshClient {
    void scale_mesh_geometry(Mesh mesh, int vertexCount, int indexCount, float factor);
}