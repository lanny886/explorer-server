package com.xyz.browser.app.modular.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImeiInfoDto {
    /**
     * 手机唯一标识符
     */
    @NotNull
    private String imei;
    /**
     * 双卡手机 imei2
     */
    private String imei2;
    /**
     * 移动设备识别码
     */
    private String meid;
    /**
     * 品牌
     */
    private String brand;
    /**
     * 机型
     */
    private String model;
    /**
     * 运营商
     */
    private String carrier;
    /*
    邀请码
     */
    @NotNull
    private String inviteCode;
    /*
    客户端时间戳
     */
    private long t;
    /*
    app编码
     */
    private String appCode;
}
