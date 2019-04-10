package com.xyz.browser.app.core.oss;

import com.aliyun.oss.OSSClient;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "guns.oss")
@Data
@NoArgsConstructor
public class OssConfig {
    private String accessKeyId;
    private String accessKeySecret;
    private String bucket;
    private String endpoint;
    private String domain;

    private String imgDir;

    @Bean
    public OSSClient getOssClient(){
        return new OSSClient(endpoint,accessKeyId,accessKeySecret);
    }
}
