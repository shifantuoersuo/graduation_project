package com.example.graduation_project.controller;

import com.example.graduation_project.service.PointsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/points")
public class PointsController {
    @Autowired
    private PointsService pointsService;

    // 获取用户积分
    @GetMapping("/{userId}")
    public int getUserPoints(@PathVariable Long userId) {
        return pointsService.getUserPoints(userId);
    }

    // 管理员增加用户积分
    @PostMapping("/add")
    public String addPoints(@RequestParam Long userId, @RequestParam int points) {
        boolean success = pointsService.updatePoints(userId, points);
        return success ? "积分已增加" : "用户不存在";
    }

    // 用户兑换积分
    @PostMapping("/redeem")
    public String redeemPoints(@RequestParam Long userId, @RequestParam int points) {
        boolean success = pointsService.redeemPoints(userId, points);
        return success ? "兑换成功" : "积分不足或用户不存在";
    }
}
