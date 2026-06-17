#include <cmath>

extern "C" {

    // @mite
    void apply_explosion_force(float* coordinatesX, float* coordinatesY, int totalObjects, float targetX, float targetY, float radius, float force) {
        for (int i = 0; i < totalObjects; i++) {
            float dx = coordinatesX[i] - targetX;
            float dy = coordinatesY[i] - targetY;

            float distance = std::sqrt(dx * dx + dy * dy);

            if (distance < radius && distance > 0.001f) {
                float forceFactor = (radius - distance) / radius * force;

                coordinatesX[i] += (dx / distance) * forceFactor;
                coordinatesY[i] += (dy / distance) * forceFactor;
            }
        }
    }

    // @mite
    float calculate_cosine_similarity(const float* vecA, const float* vecB, int length) {
        float dotProduct = 0.0f;
        float normA = 0.0f;
        float normB = 0.0f;

        for (int i = 0; i < length; i++) {
            dotProduct += vecA[i] * vecB[i];
            normA += vecA[i] * vecA[i];
            normB += vecB[i] * vecB[i];
        }

        if (normA == 0.0f || normB == 0.0f) return 0.0f;

        return dotProduct / (std::sqrt(normA) * std::sqrt(normB));
    }
}