package com.example.graduation_project.controller;

import com.example.graduation_project.entity.PointRecord;
import com.example.graduation_project.entity.User;
import com.example.graduation_project.service.PointService;
import com.example.graduation_project.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/points")
@RequiredArgsConstructor
public class PointController {

    private final PointService pointService;
    private final UserService userService;

    @GetMapping("/my")
    public ResponseEntity<List<PointRecord>> getMyPoints(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getByUsername(userDetails.getUsername());
        return ResponseEntity.ok(pointService.getUserPointRecords(user.getId()));
    }

    @GetMapping("/my/total")
    public ResponseEntity<Integer> getMyTotalPoints(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getByUsername(userDetails.getUsername());
        return ResponseEntity.ok(pointService.getUserTotalPoints(user.getId()));
    }

    @GetMapping("/top")
    public ResponseEntity<List<Map<String, Object>>> getTopUsers() {
        return ResponseEntity.ok(pointService.getTopUsers());
    }
    @GetMapping("/my/recent")
    public ResponseEntity<List<PointRecord>> getMyRecent(@AuthenticationPrincipal UserDetails userDetails,
                                                         @RequestParam int days) {
        User user = userService.getByUsername(userDetails.getUsername());
        return ResponseEntity.ok(pointService.getUserRecordsAfterDays(user.getId(), days));
    }

    @GetMapping("/my/search")
    public ResponseEntity<List<PointRecord>> getMyFiltered(@AuthenticationPrincipal UserDetails userDetails,
                                                           @RequestParam String keyword) {
        User user = userService.getByUsername(userDetails.getUsername());
        return ResponseEntity.ok(pointService.getUserRecordsByKeyword(user.getId(), keyword));
    }

}
