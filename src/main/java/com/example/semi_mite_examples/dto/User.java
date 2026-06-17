package com.example.semi_mite_examples.dto;

import java.util.ArrayList;
import java.util.List;

public class User {
    public String name;
    public int age;
    public List<User> friends = new ArrayList<>(); 

    public User(String name, int age) {
        this.name = name;
        this.age = age;
    }
}