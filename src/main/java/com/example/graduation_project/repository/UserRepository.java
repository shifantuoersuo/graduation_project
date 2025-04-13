package com.example.graduation_project.repository;

import com.example.graduation_project.Common.Role;
import com.example.graduation_project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> , JpaSpecificationExecutor<User> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    List<User> findByRole(Role role);

    Page<User> findByUsernameContainingOrEmailContaining(String username, String email, Pageable pageable);

    Page<User> findAll(Pageable pageable);


    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}

