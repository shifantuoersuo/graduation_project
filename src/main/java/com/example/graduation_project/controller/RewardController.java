package com.example.graduation_project.controller;

import com.example.graduation_project.entity.Reward;
import com.example.graduation_project.entity.RewardRecord;
import com.example.graduation_project.entity.User;
import com.example.graduation_project.service.RewardService;
import com.example.graduation_project.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import java.util.List;

@RestController
@RequestMapping("/api/rewards")
@RequiredArgsConstructor
public class RewardController {

    private final RewardService rewardService;
    private final UserService userService;

    @GetMapping("/all")
    public ResponseEntity<List<Reward>> getAllRewards() {
        return ResponseEntity.ok(rewardService.getAllRewards());
    }

    @PostMapping("/redeem/{id}")
    public ResponseEntity<RewardRecord> redeem(@AuthenticationPrincipal UserDetails userDetails,
                                               @PathVariable Long id) {
        User user = userService.getByUsername(userDetails.getUsername());
        return ResponseEntity.ok(rewardService.redeem(user.getId(), id));
    }

    @GetMapping("/records")
    public ResponseEntity<List<RewardRecord>> getMyRecords(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getByUsername(userDetails.getUsername());
        return ResponseEntity.ok(rewardService.getUserRewardRecords(user.getId()));
    }
}

