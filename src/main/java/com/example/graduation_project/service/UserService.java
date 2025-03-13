package com.example.graduation_project.service;

import com.example.graduation_project.entity.User;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface UserService {
    User updateUser(String username, User updatedUser);
    boolean changePassword(String username, String oldPassword, String newPassword);
    void deleteUser(Long id);
    List<User> getAllUsers(); // 管理员接口
    User updateUserByAdmin(Long id, User user); // 管理员接口

    User register(User user);
    String login(String username, String password);
    User getByUsername(String username);

    PageInfo<User> selectPage(Integer pageNum, Integer pageSize);
}




