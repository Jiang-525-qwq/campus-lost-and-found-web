package com.util;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import javax.servlet.http.Part;

public class FileUploadUtil {

    // 上传图片，返回保存的文件名
    public static String uploadImage(Part part, String uploadPath) throws IOException {
        if (part == null || part.getSize() == 0) {
            return null;
        }

        // 生成唯一文件名
        String fileName = UUID.randomUUID().toString() + ".jpg";
        File uploadDir = new File(uploadPath);

        if (!uploadDir.exists()) uploadDir.mkdirs();

        // 保存文件
        part.write(uploadPath + File.separator + fileName);
        return fileName;
    }
}