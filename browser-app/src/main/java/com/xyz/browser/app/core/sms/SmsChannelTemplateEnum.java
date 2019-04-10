package com.xyz.browser.app.core.sms;

import lombok.Getter;
import lombok.Setter;

/**
 * @author LiXunHuan
 * @date 2018/1/16
 * 短信通道模板
 */
public enum SmsChannelTemplateEnum {
    /**
     * 注册
     */
    REGIST_CODE_TEMPLATE("registCodeTemplate"),
    /**
     * 修改密码
     */
    UPDATE_PW_TEMPLATE("updatePWTemplate");


    /**
     * 模板名称
     */
    @Getter
    @Setter
    private String template;


    SmsChannelTemplateEnum(String template) {
        this.template = template;

    }
}
