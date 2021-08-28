package com.example.api.misc;

import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class MiscService {

    @Transactional
    public String uploadImage(MultipartFile image, String type, String id) throws IOException {

        String avatarPath = "../frontend/src/assets/" + type + "_avatar";

        File imageDirectory = new File(avatarPath);
        boolean avatarDirectoryCreated;
        if (imageDirectory.exists()) {
            avatarDirectoryCreated = true;
        } else {
            avatarDirectoryCreated = imageDirectory.mkdir();
        }
        if (avatarDirectoryCreated) {

            System.out.println("Avatar directory created");
            byte[] avatarBytes = image.getBytes();

            String imagePath = "../.." + avatarPath.substring(avatarPath.lastIndexOf("/assets")) + "/";
            String imageNewName = id + "_avatar." + FilenameUtils.getExtension(image.getOriginalFilename());
            String path = avatarPath + "/" + imageNewName;
            String dbPath = imagePath + imageNewName;

            Path storagePath = Paths.get(path);
            Files.write(storagePath, avatarBytes);
            return dbPath;
        } else {
            System.out.println("Failed to create image directory");
        }
        return "error";
    }
}
