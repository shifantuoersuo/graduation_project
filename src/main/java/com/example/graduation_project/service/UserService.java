package com.example.graduation_project.service;

import com.example.graduation_project.Common.Role;
import com.example.graduation_project.entity.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService {
    static void addActivityPoints(Long userId, int points) {
    }

    User updateUser(String username, User updatedUser);
    boolean changePassword(String username, String oldPassword, String newPassword);
    void deleteUser(Long id, String operatorUsername);
    List<User> getAllUsers(); // 管理员接口
    User updateUserByAdmin(Long id, User updatedUser, String operatorUsername); // 管理员接口

    User register(User user);
    String login(String username, String password);
    User getByUsername(String username);

    String getRole(String username);

    Page<User> searchUsers(Integer pageNum, Integer pageSize, String keyword, Role role);

    void resetPassword(Long id, String username);
}




