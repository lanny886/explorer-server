package com.xyz.browser.app.modular.system.model;

import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author stylefeng
 * @since 2019-04-10
 */
@TableName("s_ranking_miner")
public class RankingMiner extends Model<RankingMiner> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "rank", type = IdType.AUTO)
    private Integer rank;
    private String address;
    @TableField("block_reward")
    private String blockReward;
    @TableField("uncle_reward")
    private String uncleReward;
    @TableField("total_reward")
    private String totalReward;


    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBlockReward() {
        return blockReward;
    }

    public void setBlockReward(String blockReward) {
        this.blockReward = blockReward;
    }

    public String getUncleReward() {
        return uncleReward;
    }

    public void setUncleReward(String uncleReward) {
        this.uncleReward = uncleReward;
    }

    public String getTotalReward() {
        return totalReward;
    }

    public void setTotalReward(String totalReward) {
        this.totalReward = totalReward;
    }

    @Override
    protected Serializable pkVal() {
        return this.rank;
    }

    @Override
    public String toString() {
        return "RankingMiner{" +
        ", rank=" + rank +
        ", address=" + address +
        ", blockReward=" + blockReward +
        ", uncleReward=" + uncleReward +
        ", totalReward=" + totalReward +
        "}";
    }
}
