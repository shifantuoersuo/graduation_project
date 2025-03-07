package com.example.graduation_project.service;

import com.example.graduation_project.entity.User;
import com.example.graduation_project.repository.UserRepository;
import com.example.graduation_project.security.JwtUtil;
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

    @Autowired
    private JwtUtil jwtUtil;

    // 注册用户，加密密码，保存到数据库，调用UserRepository的save方法
    public User registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword())); // 加密密码，防止明文存储，保存到数据库
        return userRepository.save(user);
    }
    // 登录用户，验证密码，返回生成的JWT令牌
    public String loginUser(String username, String password) {
        Optional<User> dbUser = userRepository.findByUsername(username);
        if (dbUser.isPresent() && passwordEncoder.matches(password, dbUser.get().getPassword())) {// 验证密码
            return jwtUtil.generateToken(username); // 生成JWT令牌
        }
        return null;
    }

    // 根据用户名查找用户，调用UserRepository的findByUsername方法
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}

