package com.xyz.browser.app.modular.api.vo;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.xyz.browser.common.constant.Reward;
import lombok.Data;

import java.io.Serializable;

@Data
public class BlockVo implements Serializable {
    private String difficulty;
    private String extraData;
    private String gasLimit;
    private String gasUsed;
    private String hash;
    private String miner;
    private String nonce;
    private String number;
    private String parentHash;
    private String sha3Uncles;
    private String size;
    private String timestamp;
    private String totalDifficulty;

    private String txnCount;
    private String ctxnCount;

    private String blockReward= Reward.BLOCK_REWARD;
    private String unclesReward=Reward.UNCLES_REWARD;

    private String interval;

}
