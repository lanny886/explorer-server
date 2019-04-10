package com.xyz.browser.app.modular.system.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author stylefeng
 * @since 2019-03-30
 */
@TableName("b_rt_uncle")
public class RtUncle extends Model<RtUncle> {

    private static final long serialVersionUID = 1L;

    private Long number;
    @TableField("block_number")
    private Long blockNumber;
    private String miner;
    private String reward;
    private String t;
    @TableId("hash")
    private String hash;


    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }

    public Long getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(Long blockNumber) {
        this.blockNumber = blockNumber;
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

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    @Override
    protected Serializable pkVal() {
        return this.hash;
    }

    @Override
    public String toString() {
        return "RtUncle{" +
        ", number=" + number +
        ", blockNumber=" + blockNumber +
        ", miner=" + miner +
        ", reward=" + reward +
        ", t=" + t +
        ", hash=" + hash +
        "}";
    }
}
