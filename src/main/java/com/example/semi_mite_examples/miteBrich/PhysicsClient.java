package com.example.semi_mite_examples.miteBrich;

import com.example.semi_mite_examples.dto.Particle;
import org.example.client.MiteClient;
import org.example.memory.MiteArray;

@MiteClient //if we don't know the file path, but we are know that it exists, mite will handle it automatically
public interface PhysicsClient {

    void apply_explosion_force(
            MiteArray coordinatesX,
            MiteArray coordinatesY,
            int totalObjects,
            float targetX,
            float targetY,
            float radius,
            float force
    );

    float calculate_cosine_similarity(MiteArray vecA, MiteArray vecB, int length);

    void process_particle(Particle particle);
}