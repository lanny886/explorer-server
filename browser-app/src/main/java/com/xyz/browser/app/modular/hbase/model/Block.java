package com.xyz.browser.app.modular.hbase.model;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("block")
public class Block extends Model<Block> {
    @TableField("difficulty")
    private String difficulty;
    @TableField("extraData")
    private String extraData;
    @TableField("gasLimit")
    private String gasLimit;
    @TableField("gasUsed")
    private String gasUsed;
    @TableId(value = "hash")
    private String hash;
    @TableField("logsBloom")
    private String logsBloom;
    @TableField("miner")
    private String miner;
    @TableField("mixHash")
    private String mixHash;
    @TableField("nonce")
    private String nonce;
    @TableField("number")
    private String number;
    @TableField("parentHash")
    private String parentHash;
    @TableField("receiptsRoot")
    private String receiptsRoot;
    @TableField("sha3Uncles")
    private String sha3Uncles;
    @TableField("size")
    private String size;
    @TableField("stateRoot")
    private String stateRoot;
    @TableField("timestamp")
    private String timestamp;
    @TableField("totalDifficulty")
    private String totalDifficulty;
    @TableField("transactions")
    private String transactions;//jsonStr arr
    @TableField("transactionsRoot")
    private String transactionsRoot;
    @TableField("uncles")
    private String uncles;//jsonStr arr

    @Override
    protected Serializable pkVal() {
        return this.hash;
    }
}
