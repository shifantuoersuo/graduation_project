package com.example.graduation_project.service;

import com.example.graduation_project.entity.User;

public interface UserService {
    User register(User user);
    String login(String username, String password);
    User getByUsername(String username);
}




