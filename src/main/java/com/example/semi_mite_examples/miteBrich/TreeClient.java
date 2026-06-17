package com.example.semi_mite_examples.miteBrich;

import com.example.semi_mite_examples.dto.Node;
import org.example.client.MiteClient;

@MiteClient(script = "cppScripts/tree_processor.cpp")
public interface TreeClient {
    void invert_and_increment_tree(Node root);
}