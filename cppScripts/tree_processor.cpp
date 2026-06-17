#include <iostream>

struct Node
{
    int value;
    Node *left;
    Node *right;
};

//@mite
extern "C" void invert_and_increment_tree(Node *root)
{
    if (root == nullptr)
        return;

    Node *temp = root->left;
    root->left = root->right;
    root->right = temp;

    root->value += 100;

    invert_and_increment_tree(root->left);
    invert_and_increment_tree(root->right);
}