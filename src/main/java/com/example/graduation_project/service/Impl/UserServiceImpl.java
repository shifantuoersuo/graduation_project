package com.example.graduation_project.service.Impl;

import com.example.graduation_project.Common.JwtUtil;
import com.example.graduation_project.Common.Role;
import com.example.graduation_project.entity.User;
import com.example.graduation_project.exception.CustomerException;
import com.example.graduation_project.repository.UserRepository;
import com.example.graduation_project.service.UserService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

        boolean isUpdated = false;// 是否有更新


        // 用户名处理
        if (updatedUser.getUsername() != null) {
            String newUsername = updatedUser.getUsername().trim();
            if (newUsername.isEmpty()) {
                throw new CustomerException("用户名不能为空");
            }
            if (!newUsername.equals(user.getUsername())) {
                if (userRepository.existsByUsername(newUsername)) {
                    throw new CustomerException("用户名已存在");
                }
                user.setUsername(newUsername);
                isUpdated = true;
            }
        }

        // 邮箱处理
        if (updatedUser.getEmail() != null) {
            String newEmail = updatedUser.getEmail();
            if (!newEmail.equals(user.getEmail())) {
                if (userRepository.existsByEmail(newEmail)) {
                    throw new CustomerException("邮箱已存在");
                }
                user.setEmail(newEmail);
                isUpdated = true;
            }
        }


        // 头像处理
        if (updatedUser.getAvatar() != null) {
            user.setAvatar(updatedUser.getAvatar());
            isUpdated = true;
        }

        // 更新时间（仅在数据变更时更新）
        if (isUpdated) {
            user.setUpdateTime(LocalDateTime.now());
        }

        return userRepository.save(user);
    }

    /**
     * 根据管理员提供的信息更新用户信息
     */
    @Override
    public User updateUserByAdmin(Long id, User updatedUser, String operatorUsername) {
        User operator = getByUsername(operatorUsername);
        User target = userRepository.findById(id).orElseThrow(() -> new CustomerException("用户不存在"));

        // 如果对方是 ADMIN/SUPER_ADMIN，当前用户不是 SUPER_ADMIN，不允许改
        if ((target.getRole() == Role.ADMIN || target.getRole() == Role.SUPER_ADMIN)
                && operator.getRole() != Role.SUPER_ADMIN) {
            throw new CustomerException("权限不足，无法修改管理员信息");
        }
        // 新增：禁止修改超级管理员
        if (target.getRole() == Role.SUPER_ADMIN) {
            throw new CustomerException("禁止修改超级管理员信息");
        }
        target.setUsername(updatedUser.getUsername());
        target.setEmail(updatedUser.getEmail());
        target.setAvatar(updatedUser.getAvatar());
        target.setRole(updatedUser.getRole());
        target.setActivityCount(updatedUser.getActivityCount());
        target.setTotalPoints(updatedUser.getTotalPoints());
        target.setUpdateTime(LocalDateTime.now());
        return userRepository.save(target);
    }


    /**
     * 修改用户密码的方法
     * 根据用户名查找用户，验证旧密码是否正确，然后更新为新密码
     */
    @Override
    public boolean changePassword(String username, String oldPassword, String newPassword) {
        System.out.println("changePassword"+username+oldPassword+newPassword);
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
        user.setUpdateTime(LocalDateTime.now());
        userRepository.save(user);
        return true;
    }

    @Override
    public void deleteUser(Long id, String operatorUsername) {
        User operator = getByUsername(operatorUsername);
        User target = userRepository.findById(id).orElseThrow(() -> new CustomerException("用户不存在"));

        if ((target.getRole() == Role.ADMIN || target.getRole() == Role.SUPER_ADMIN)
                && operator.getRole() != Role.SUPER_ADMIN) {
            throw new CustomerException("权限不足，不能删除管理员");
        }

        userRepository.deleteById(id);
    }


    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Page<User> searchUsers(Integer pageNum, Integer pageSize, String keyword, Role role) {
        if (pageNum < 1) throw new CustomerException("页码不能小于1");
        int adjustedPageNum = pageNum - 1;
        Pageable pageable = PageRequest.of(adjustedPageNum, pageSize, Sort.by("createTime").descending());

        // 构建动态查询条件
        Specification<User> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(keyword)) {
                Predicate usernamePredicate = cb.like(root.get("username"), "%" + keyword + "%");
                Predicate emailPredicate = cb.like(root.get("email"), "%" + keyword + "%");
                predicates.add(cb.or(usernamePredicate, emailPredicate));
            }

            if (role != null) {
                predicates.add(cb.equal(root.get("role"), role));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return userRepository.findAll(spec, pageable);
    }

    @Override
    public void resetPassword(Long id, String operatorUsername) {
        User operator = getByUsername(operatorUsername);
        User target = userRepository.findById(id).orElseThrow(() -> new CustomerException("用户不存在"));

        // 权限校验
        if ((target.getRole() == Role.ADMIN || target.getRole() == Role.SUPER_ADMIN)
                && operator.getRole() != Role.SUPER_ADMIN) {
            throw new CustomerException("权限不足");
        }

        // 重置密码逻辑
        target.setPassword(passwordEncoder.encode("12345678"));
        userRepository.save(target);
    }


    @Override
    public String getRole(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomerException("用户不存在"));
        return user.getRole().toString();
    }
}
