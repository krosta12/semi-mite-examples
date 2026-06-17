#include <cmath>
#include <iostream>

struct Particle {
    float x;
    float y;
    float speed;
    int id;
};

extern "C" {

    // @mite
    void process_particle(Particle* particle) {
        particle->x += 10.5f;
        particle->y += 20.5f;
        particle->speed *= 2.0f;

        std::cout << "[C++ Native] handled object ID: " << particle->id
                  << ", new cords: (" << particle->x << ", " << particle->y << ")" << std::endl;
    }
}