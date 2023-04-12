package com.example.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.UUID;

public class FileUntils {
    //获取随机且唯一的文件名称
    public static String getUIIDFileName(MultipartFile upload) {
        //获取上传文件的名称
        String originalFileName = upload.getOriginalFilename();
        // 自定义上传文件名
        String fileName = UUID.randomUUID().toString();
        // 获取上传文件扩展名
        String suffix = "";
        int beginIndex = originalFileName.lastIndexOf(".");
        if (beginIndex > 0) {
            suffix = originalFileName.substring(beginIndex);
        }
        String fullFilename = fileName + suffix;

        return fullFilename;
    }

    //保存文件到未编译路径（这里是编译前的相同路径）
    //oldUrl: 原文件路径,newUrl: 保存文件路劲,filename: 文件名称
    public static void saveFile(String oldUrl, String newUrl, String fileName) {
        File javaFile = new File(newUrl);
        //判断未编译路径是否创建
        if (!javaFile.isDirectory()) {
            javaFile.mkdirs();
        }
        //保存上传目录的文件到未编译路劲
        try {
            copyFile(oldUrl + "/" + fileName, newUrl + "/" + fileName);
        } catch (IOException e) {
            throw new RuntimeException("文件保存失败");
        }
    }

    //复制文件
    private static void copyFile(String oldUrl, String copyUrl) throws IOException {
        BufferedInputStream fr = new BufferedInputStream(new FileInputStream(oldUrl));
        BufferedOutputStream fs = new BufferedOutputStream(new FileOutputStream(copyUrl));

        byte[] bys = new byte[1024];
        int len;
        while ((len = fr.read(bys)) != -1) {      //fs.read(bys)返回的是bys中的字符个数
            fs.write(bys, 0, len);
        }

        fs.close();
        fr.close();
    }
}
