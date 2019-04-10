package com.xyz.browser.app.core.sms;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Map;
@Configuration
@ConfigurationProperties(prefix = "guns.sms")
@Data
public class SmsProperties {
    private String accessKey;
    private String secretKey;
    private Map<String,String> templates;
    private String signName = "手机部落";
}
