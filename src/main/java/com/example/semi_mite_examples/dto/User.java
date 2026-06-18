package com.example.semi_mite_examples.dto;

import org.example.annotation.MiteStruct;

import java.util.ArrayList;
import java.util.List;

@MiteStruct
public class User {
    public String name;
    public int age;
    public List<User> friends = new ArrayList<>(); 

    public User(String name, int age) {
        this.name = name;
        this.age = age;
    }
}