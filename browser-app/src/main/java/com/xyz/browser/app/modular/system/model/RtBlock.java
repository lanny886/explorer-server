package com.xyz.browser.app.modular.system.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;

/**
 * <p>
 * 实时block
 * </p>
 *
 * @author stylefeng
 * @since 2019-03-21
 */
@TableName("b_rt_block")
public class RtBlock extends Model<RtBlock> {

    private static final long serialVersionUID = 1L;

    private Long number;
    private String miner;
    private String reward;
    private String t;
    @TableField("txn_count")
    private String txnCount;
    @TableField("uncle_count")
    private String uncleCount;
    @TableField("gas_used")
    private String gasUsed;
    @TableField("gas_limit")
    private String gasLimit;
    @TableField("avg_gas_price")
    private String avgGasPrice;
    @TableId(value = "hash")
    private String hash;


    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }

    public String getMiner() {
        return miner;
    }

    public void setMiner(String miner) {
        this.miner = miner;
    }

    public String getReward() {
        return reward;
    }

    public void setReward(String reward) {
        this.reward = reward;
    }

    public String getT() {
        return t;
    }

    public void setT(String t) {
        this.t = t;
    }

    public String getTxnCount() {
        return txnCount;
    }

    public void setTxnCount(String txnCount) {
        this.txnCount = txnCount;
    }

    public String getUncleCount() {
        return uncleCount;
    }

    public void setUncleCount(String uncleCount) {
        this.uncleCount = uncleCount;
    }

    public String getGasUsed() {
        return gasUsed;
    }

    public void setGasUsed(String gasUsed) {
        this.gasUsed = gasUsed;
    }

    public String getGasLimit() {
        return gasLimit;
    }

    public void setGasLimit(String gasLimit) {
        this.gasLimit = gasLimit;
    }

    public String getAvgGasPrice() {
        return avgGasPrice;
    }

    public void setAvgGasPrice(String avgGasPrice) {
        this.avgGasPrice = avgGasPrice;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    @Override
    protected Serializable pkVal() {
        return this.number;
    }

    @Override
    public String toString() {
        return "RtBlock{" +
        ", number=" + number +
        ", miner=" + miner +
        ", reward=" + reward +
        ", t=" + t +
        ", txnCount=" + txnCount +
        ", uncleCount=" + uncleCount +
        ", gasUsed=" + gasUsed +
        ", gasLimit=" + gasLimit +
        ", avgGasPrice=" + avgGasPrice +
        ", hash=" + hash +
        "}";
    }
}
