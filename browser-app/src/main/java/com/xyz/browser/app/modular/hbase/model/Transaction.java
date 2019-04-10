package com.xyz.browser.app.modular.hbase.model;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;
@Data
@TableName("transaction")
public class Transaction extends Model<Transaction> {
    @TableField("blockHash")
    private String blockHash;
    @TableField("blockNumber")
    private String blockNumber;
    @TableField("contractAddress")
    private String contractAddress;
    @TableField("cumulativeGasUsed")
    private String cumulativeGasUsed;
    @TableField("tfrom")
    private String from;
    @TableField("gasUsed")
    private String gasUsed;
    @TableField("logs")
    private String logs;//jsonStr arr
    @TableField("logsBloom")
    private String logsBloom;
    @TableField("status")
    private String status;
    @TableField("tto")
    private String to;
    @TableId(value = "transactionHash")
    private String transactionHash;
    @TableField("transactionIndex")
    private String transactionIndex;

    @Override
    protected Serializable pkVal() {
        return this.transactionHash;
    }
}
