package com.example.graduation_project.repository;

import com.example.graduation_project.entity.Point;
import com.example.graduation_project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PointRepository extends JpaRepository<Point, Long> {
    Optional<Point> findByUser(User user);
}
