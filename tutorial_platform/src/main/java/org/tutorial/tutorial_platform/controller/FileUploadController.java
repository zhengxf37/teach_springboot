package org.tutorial.tutorial_platform.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * FileUploadController 文件上传控制器
 * 上传文件
 * @author: 周宏杰
 *
 */
@RestController
@RequestMapping("/api/file")
public class FileUploadController {

    @Value("${file.upload-dir}")
    private String uploadDir; // 配置的根目录（如 ../uploads）

    /**
     * 多文件上传接口，绑定用户ID
     * @param userId 用户唯一标识
     * @param files 上传的多个文件
     * @return 返回文件访问路径列表
     */
    @PostMapping("/upload/{userId}")
    public ResponseEntity<List<String>> uploadFiles(
            @PathVariable Long userId,
            @RequestParam("files") MultipartFile[] files) {

        List<String> fileUrls = new ArrayList<>();

        if (files.length == 0) {
            return ResponseEntity.badRequest().body(fileUrls);
        }

        try {
            // 构建用户专属目录：../uploads/userId/，但不一定已经创建了文件夹
            File userDir = new File(uploadDir + File.separator + userId);
            if (!userDir.exists()) {
                userDir.mkdirs();
            }

            for (MultipartFile file : files) {
                if (file.isEmpty()) continue;

                String originalFilename = file.getOriginalFilename();
                String fileExtension = "";

                if (originalFilename != null && originalFilename.contains(".")) {
                    fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
                }

                String uniqueFilename = UUID.randomUUID() + fileExtension;
                File dest = new File(userDir.getAbsolutePath() + File.separator + uniqueFilename);

                // 保存文件到本地
                file.transferTo(dest);

                // 生成可访问路径
                String fileUrl = "/uploads/" + userId + "/" + uniqueFilename;
                //返回/uploads/123/uuid.png
                //访问http://localhost:8080/uploads/123/uuid.png
                fileUrls.add(fileUrl);
            }

            return ResponseEntity.ok(fileUrls);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Collections.emptyList());
        }
    }
    @GetMapping("/list/{userId}")
    public ResponseEntity<List<String>> listUserFiles(@PathVariable Long userId) {
        List<String> fileUrls = new ArrayList<>();

        // 构建用户目录路径
        File userDir = new File(uploadDir + File.separator + userId);
        if (!userDir.exists()) {
            return ResponseEntity.ok(fileUrls); // 目录不存在，返回空列表
        }

        // 遍历目录下的所有文件
        File[] files = userDir.listFiles();
        if (files != null) {
            for (File file : files) {
                String fileName = file.getName();
                String fileUrl = "/uploads/" + userId + "/" + fileName;
                fileUrls.add(fileUrl);
            }
        }

        return ResponseEntity.ok(fileUrls);
    }

}
