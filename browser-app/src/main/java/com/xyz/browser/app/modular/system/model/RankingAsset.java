package com.xyz.browser.app.modular.system.model;

import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.annotations.TableId;
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
@TableName("s_ranking_asset")
public class RankingAsset extends Model<RankingAsset> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "rank", type = IdType.AUTO)
    private Integer rank;
    private String address;
    private String asset;


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

    public String getAsset() {
        return asset;
    }

    public void setAsset(String asset) {
        this.asset = asset;
    }

    @Override
    protected Serializable pkVal() {
        return this.rank;
    }

    @Override
    public String toString() {
        return "RankingAsset{" +
        ", rank=" + rank +
        ", address=" + address +
        ", asset=" + asset +
        "}";
    }
}
