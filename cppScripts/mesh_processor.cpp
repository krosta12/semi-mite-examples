#include <iostream>

struct Mesh
{
    const char *name;
    float *vertices;
    int *indices;
};

//@mite
extern "C" void scale_mesh_geometry(Mesh *mesh, int vertexCount, int indexCount, float factor)
{
    if (mesh == nullptr)
        return;

    std::cout << "[C++ Native] Processing mesh: " << mesh->name << std::endl;

    for (int i = 0; i < vertexCount; i++)
    {
        mesh->vertices[i] *= factor;
    }

    for (int i = 0; i < indexCount; i++)
    {
        mesh->indices[i] += 1;
    }
}