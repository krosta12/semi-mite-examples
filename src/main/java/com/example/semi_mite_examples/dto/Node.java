package com.example.semi_mite_examples.dto;

public class Node {
    public int value;
    public Node left;
    public Node right;

    public Node(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Node[val=" + value + ", L=" + (left != null ? left.value : "null") + ", R=" + (right != null ? right.value : "null") + "]";
    }
}