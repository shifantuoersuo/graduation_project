package com.example.graduation_project.repository;

import com.example.graduation_project.entity.PointRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface PointRecordRepository extends JpaRepository<PointRecord, Long> {
    List<PointRecord> findByUserId(Long userId);

    @Query("SELECT SUM(p.points) FROM PointRecord p WHERE p.userId = :userId")
    Integer getTotalPointsByUserId(@Param("userId") Long userId);

    @Query("SELECT p.userId, SUM(p.points) as total FROM PointRecord p GROUP BY p.userId ORDER BY total DESC")
    List<Object[]> findTopUsersByPoints();

    @Query("SELECT p FROM PointRecord p WHERE p.userId = :userId AND p.createTime >= :start")
    List<PointRecord> findByUserIdAndAfterTime(@Param("userId") Long userId, @Param("start") LocalDateTime start);

    @Query("SELECT p FROM PointRecord p WHERE p.userId = :userId AND p.description LIKE %:keyword%")
    List<PointRecord> findByUserIdAndDescriptionContaining(@Param("userId") Long userId, @Param("keyword") String keyword);

    @Query("SELECT p FROM PointRecord p WHERE DATE(p.createTime) = :date")
    List<PointRecord> findByDate(@Param("date") LocalDate date);
    @Query("SELECT p FROM PointRecord p WHERE p.createTime >= :start AND p.createTime < :end")
    List<PointRecord> findByDateRange(@Param("start") LocalDateTime start,
                                      @Param("end") LocalDateTime end);
}