package com.example.semi_mite_examples;

import org.springframework.stereotype.Service;

@Service
public class PhysicsService {
    public void runJavaPhysics(float[] x, float[] y, float tx, float ty, float r, float f) {
        int len = x.length;
        for (int i = 0; i < len; i++) {
            float dx = x[i] - tx;
            float dy = y[i] - ty;

            float distance = (float) Math.sqrt(dx * dx + dy * dy);

            if (distance < r && distance > 0.001f) {
                float forceFactor = (r - distance) / r * f;
                x[i] += (dx / distance) * forceFactor;
                y[i] += (dy / distance) * forceFactor;
            }
        }
    }
}