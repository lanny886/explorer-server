package com.xyz.browser.app.modular.system.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;

/**
 * <p>
 * 实时transaction
 * </p>
 *
 * @author stylefeng
 * @since 2019-03-21
 */
@TableName("t_rt_txn")
public class RtTxn extends Model<RtTxn> {

    private static final long serialVersionUID = 1L;
    @TableId(value = "hash")
    private String hash;
    @TableField("block_number")
    private Long blockNumber;
    private String from;
    private String to;
    private String value;
    private String t;
    @TableField("txn_fee")
    private String txnFee;
    private String status;
    @TableField("block_hash")
    private String blockHash;


    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Long getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(Long blockNumber) {
        this.blockNumber = blockNumber;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getT() {
        return t;
    }

    public void setT(String t) {
        this.t = t;
    }

    public String getTxnFee() {
        return txnFee;
    }

    public void setTxnFee(String txnFee) {
        this.txnFee = txnFee;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBlockHash() {
        return blockHash;
    }

    public void setBlockHash(String blockHash) {
        this.blockHash = blockHash;
    }

    @Override
    protected Serializable pkVal() {
        return this.hash;
    }

    @Override
    public String toString() {
        return "RtTxn{" +
        ", hash=" + hash +
        ", blockNumber=" + blockNumber +
        ", from=" + from +
        ", to=" + to +
        ", value=" + value +
        ", t=" + t +
        ", txnFee=" + txnFee +
        ", status=" + status +
        ", blockHash=" + blockHash +
        "}";
    }
}
