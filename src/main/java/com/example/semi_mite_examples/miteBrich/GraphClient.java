package com.example.semi_mite_examples.miteBrich;

import com.example.semi_mite_examples.dto.User;
import org.example.client.MiteClient;

@MiteClient(script = "cppScripts/graph_processor.cpp") //path is decorative
public interface GraphClient {
    void celebrate_birthday_system(User mainUser);
}