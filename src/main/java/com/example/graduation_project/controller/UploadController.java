package com.example.graduation_project.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

/**
 * 这个类用于处理图片上传的接口
 */
@RestController
@RequestMapping("/api/upload")
public class UploadController {

    private final String uploadDir = "D:\\卓面文档\\java开发\\Graduation project\\vue\\public\\uploads"; // 存放图片的绝对目录
    @PostMapping("/image")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        System.out.println("Upload request received");
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "文件为空"));
        }

        try {
            // 创建文件夹
            File dir = new File(uploadDir);
            // 打印绝对路径
            System.out.println("Absolute path of upload directory: " + dir.getAbsolutePath());
            if (!dir.exists() && !dir.mkdirs()) {
                throw new IOException("无法创建文件夹");
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String fileName = UUID.randomUUID() + "_" + originalFilename;
            File dest = new File(uploadDir + "\\" + fileName);
//            File dest = new File(uploadDir + "/" + fileName);

            file.transferTo(dest);

            String fileUrl = "/uploads/" + fileName; // 访问路径
            return ResponseEntity.ok(Map.of("url", fileUrl));
        } catch (IOException e) {
            e.printStackTrace(); // 打印异常堆栈信息
            return ResponseEntity.internalServerError().body(Map.of("error", "上传失败"));
        }
    }
}

