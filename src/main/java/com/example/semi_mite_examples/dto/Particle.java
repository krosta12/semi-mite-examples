package com.example.semi_mite_examples.dto;

public class Particle {
    public float x;
    public float y;
    public float speed;
    public int id;

    public Particle(int id, float x, float y, float speed) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.speed = speed;
    }

    @Override
    public String toString() {
        return String.format("Particle[id=%d, x=%.2f, y=%.2f, speed=%.2f]", id, x, y, speed);
    }
}