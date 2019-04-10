package com.xyz.browser.app.modular.hbase.model;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("btransaction")
public class Btransaction extends Model<Btransaction> {
    @TableId("hash")
    private String hash;
    @TableField("blockHash")
    private String blockHash;
    @TableField("blockNumber")
    private String blockNumber;
    @TableField("tfrom")
    private String from;
    @TableField("gas")
    private String gas;
    @TableField("gasPrice")
    private String gasPrice;
    @TableField("input")
    private String input;
    @TableField("nonce")
    private String nonce;
    @TableField("tto")
    private String to;
    @TableField("transactionIndex")
    private String transactionIndex;
    @TableId(value = "tvalue")
    private String value;
    @TableId(value = "v")
    private String v;
    @TableId(value = "r")
    private String r;
    @TableId(value = "s")
    private String s;
    @TableId(value = "timestamp")
    private String timestamp;



    @Override
    protected Serializable pkVal() {
        return this.hash;
    }
}
