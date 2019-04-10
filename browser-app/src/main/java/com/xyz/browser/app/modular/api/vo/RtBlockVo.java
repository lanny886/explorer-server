package com.xyz.browser.app.modular.api.vo;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 实时block
 * </p>
 *
 * @author stylefeng
 * @since 2019-03-21
 */
@Data
public class RtBlockVo {

    private Long number;
    private String miner;
    private String reward;
    private String t;
    private String txnCount;
    private String uncleCount;
    private String gasUsed;
    private String gasLimit;
    private String avgGasPrice;
    private String hash;

    private String interval;


}
