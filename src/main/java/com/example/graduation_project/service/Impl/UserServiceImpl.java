package com.example.graduation_project.service.Impl;

import com.example.graduation_project.Common.JwtUtil;
import com.example.graduation_project.Common.Role;
import com.example.graduation_project.entity.User;
import com.example.graduation_project.exception.CustomerException;
import com.example.graduation_project.repository.UserRepository;
import com.example.graduation_project.service.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public User register(User user) {
        // 检查用户名是否已存在
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new CustomerException("用户名已存在");
        }

        // 检查邮箱是否已存在
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new CustomerException("邮箱已存在");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);
        return userRepository.save(user);
    }


    /**
     * 用户登录方法，验证用户名和密码并生成JWT令牌
     * @param username 用户名
     * @param password 密码
     * @return 生成的JWT令牌
     * @throws RuntimeException 如果凭证无效
     */
    @Override
    public String login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomerException("用户名无效"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new CustomerException("密码无效");
        }

        return jwtUtil.generateToken(new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
        ));
    }


    @Override
    public User getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomerException("用户不存在"));
    }

    // 更新用户信息的方法
    @Override
    public User updateUser(String username, User updatedUser) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomerException("用户不存在"));

        user.setEmail(updatedUser.getEmail());
        user.setAvatar(updatedUser.getAvatar()); // 设置头像
        user.setUpdateTime(LocalDateTime.now());

        return userRepository.save(user);
    }

    /**
     * 根据管理员提供的信息更新用户信息
     */
    @Override
    public User updateUserByAdmin(Long id, User updatedUser) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomerException("用户不存在"));

        user.setUsername(updatedUser.getUsername());
        user.setEmail(updatedUser.getEmail());
        user.setAvatar(updatedUser.getAvatar()); // 设置头像
        user.setRole(updatedUser.getRole());
        user.setUpdateTime(LocalDateTime.now());

        return userRepository.save(user);
    }

    /**
     * 修改用户密码的方法
     * 根据用户名查找用户，验证旧密码是否正确，然后更新为新密码
     */
    @Override
    public boolean changePassword(String username, String oldPassword, String newPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomerException("用户不存在"));
        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new CustomerException("旧密码无效");
        }

        // 验证新旧密码是否相同
        if (passwordEncoder.matches(oldPassword, passwordEncoder.encode(newPassword))) {
            throw new CustomerException("新密码不能与旧密码相同");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return true;
    }

    @Override
    public void deleteUser(Long id) {// 检查用户是否存在
        if (userRepository.findById(id).isEmpty()) {
            throw new CustomerException("用户不存在");
        }
        // 如果用户存在，则尝试删除
            userRepository.deleteById(id);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public PageInfo<User> selectPage(Integer pageNum, Integer pageSize) {
        // 开启分页查询
        PageHelper.startPage(pageNum, pageSize);
        List<User> list = userRepository.findByRole(Role.USER);
        return PageInfo.of(list);
    }
}
