package com.example.graduation_project.service;

import com.example.graduation_project.entity.User;
import com.example.graduation_project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // 注册用户，加密密码，保存到数据库，调用UserRepository的save方法
    public User registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword())); // 加密密码，防止明文存储，保存到数据库
        return userRepository.save(user);
    }
    // 根据用户名查找用户，调用UserRepository的findByUsername方法
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}

