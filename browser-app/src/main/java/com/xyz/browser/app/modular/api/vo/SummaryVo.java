package com.xyz.browser.app.modular.api.vo;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 概要信息
 * </p>
 *
 * @author stylefeng
 * @since 2019-03-19
 */
@Data
public class SummaryVo{

    private static final long serialVersionUID = 1L;

    private String price;
    private String exchangeRate;
    private String upDown;
    private String latestBlock;
    private String avgBlockTime;
    private String ttc;
    private String tps;
    private String marketCap;
    private String difficulty;
    private String avgHashRate;

}
