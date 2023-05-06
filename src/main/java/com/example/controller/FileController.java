package com.example.controller;

import com.example.common.Result;
import com.example.util.UUIDUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * 文件上传和下载
 */
@CrossOrigin
@Slf4j
@RestController
@RequestMapping("/file")
public class FileController {

    @Value("${PImage.path}")
    private String basePath;

    /**
     * 文件上传
     *
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public Result upload(MultipartFile file) {
        //原始文件名
        String originalFileName = file.getOriginalFilename(); //xxx.jpg
        //使用UUID重新生成文件名，防止文件名称重复造成文件覆盖
        String fileName = UUIDUtils.getUUID();
        // 获取上传文件扩展名
        String suffix = "";
        //判断 . 前是否有数据，有则进行切割
        int beginIndex = originalFileName.lastIndexOf(".");
        if (beginIndex > 0) {
            suffix = originalFileName.substring(beginIndex);
        }
        fileName = fileName + suffix; //uuid.jpg
        //创建一个目录对象
        File dir = new File(basePath);
        //判断当前目录是否存在
        if (!dir.exists()) {
            //目录不存在，需要创建
            dir.mkdirs();
        }
        try {
            //将临时文件转存到指定位置
            file.transferTo(new File(basePath + fileName));
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("图片上传失败");
        }
        return Result.success(fileName);
    }

    /**
     * 文件下载
     *
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) {
        try {
            //输入流，通过输入流读取文件内容
            FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));
            //输出流，通过输出流将文件写回浏览器
            System.out.println(basePath);
            ServletOutputStream outputStream = response.getOutputStream();
            response.setContentType("image/jpeg");
            //读写文件
            int len = 0;
            byte[] bytes = new byte[1024]; //定义为 1KB
            while ((len = fileInputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
                outputStream.flush();
            }
            //关闭资源
            outputStream.close();
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
