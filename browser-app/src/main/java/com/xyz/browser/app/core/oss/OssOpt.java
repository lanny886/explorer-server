package com.xyz.browser.app.core.oss;

import com.xyz.browser.app.config.properties.GunsProperties;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Date;
import java.util.UUID;

@Component
@Slf4j
public class OssOpt {

    @Autowired
    private OssConfig ossConfig;

    @Autowired
    private OSSClient client;


    private static FastDateFormat sdf = FastDateFormat.getInstance("yyyyMMdd");

    public String upload(String absoluteFilename) throws IOException {
        return uploadToYun(new File(absoluteFilename));
    }

    public String upload(MultipartFile file) throws IOException {
        String fileName=file.getOriginalFilename();
        Long fileSize = file.getSize();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        ObjectMetadata metadata= new ObjectMetadata();
        metadata.setContentLength(fileSize);
        if (StringUtils.lowerCase(suffix).matches("^\\.(jpg|jpeg|png|bmp|gif)$")) {
            metadata.setContentType("image/jpeg;charset=UTF-8");
        }
        if (StringUtils.lowerCase(suffix).matches("^\\.(html)$")) {
            metadata.setContentType("text/html;charset=UTF-8");
        }
        if (StringUtils.lowerCase(suffix).matches("^\\.(css)$")) {
            metadata.setContentType("text/css;charset=UTF-8");
        }
        String relativePath = ossConfig.getImgDir()+"/"+sdf.format(new Date())
                + "/" + UUID.randomUUID().toString().replaceAll("-", "") + suffix;
        //上传文件
        PutObjectResult putResult= client.putObject(ossConfig.getBucket(), relativePath, file.getInputStream(),metadata);
        log.info("阿里云OSS上传结果：" + putResult.getETag());
        return ossConfig.getDomain()+ "/"+relativePath;

    }

    private String uploadToYun(File file) throws FileNotFoundException {
        InputStream content = new FileInputStream(file);
        // 创建上传Object的Metadata
        ObjectMetadata meta = new ObjectMetadata();
        // 必须设置ContentLength
        meta.setContentLength(file.length());

        String suffix = file.getName().substring(file.getName().lastIndexOf("."));
        if (StringUtils.lowerCase(suffix).matches("^\\.(jpg|jpeg|png|bmp|gif)$")) {
            meta.setContentType("image/jpeg;charset=UTF-8");
        }

        if (StringUtils.lowerCase(suffix).matches("^\\.(html)$")) {
            meta.setContentType("text/html;charset=UTF-8");
        }

        if (StringUtils.lowerCase(suffix).matches("^\\.(css)$")) {
            meta.setContentType("text/css;charset=UTF-8");
        }

        String relativePath = ossConfig.getImgDir()+"/"+sdf.format(new Date())
                + "/" + UUID.randomUUID().toString().replaceAll("-", "") + suffix;
        PutObjectResult result = client.putObject(ossConfig.getBucket(), relativePath, content, meta);
        log.info("阿里云OSS上传结果：" + result.getETag());
        return ossConfig.getDomain()+ "/"+relativePath;
    }

    public String uploadToYun(InputStream content,String str) throws Exception {
        ObjectMetadata meta = new ObjectMetadata();

        meta.setContentType("image/jpeg;charset=UTF-8");

        // 必须设置ContentLength
        meta.setContentLength(content.available());


        String relativePath = ossConfig.getImgDir()+"/"+sdf.format(new Date())
                + "/" + UUID.randomUUID().toString()+"."+str;

        PutObjectResult result = client.putObject(ossConfig.getBucket(),relativePath , content, meta);


        log.info("阿里云OSS上传结果：" + result.getETag());
        return ossConfig.getDomain()+"/"+relativePath;
    }
}
