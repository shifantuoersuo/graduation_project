package com.example.graduation_project.entity;

import com.example.graduation_project.Common.ActivityStatus;
import lombok.Data;

@Data
public class ReviewRequest {
    private ActivityStatus status;
    private int points;
    private String reason;

}
