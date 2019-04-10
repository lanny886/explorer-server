package com.xyz.browser.app.modular.system.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;

/**
 * <p>
 * 概要信息
 * </p>
 *
 * @author stylefeng
 * @since 2019-03-19
 */
@TableName("s_summary")
public class Summary extends Model<Summary> {

    private static final long serialVersionUID = 1L;
    @TableId("id")
    private Integer id;
    private String price;
    @TableField("exchange_rate")
    private String exchangeRate;
    @TableField("up_down")
    private String upDown;
    @TableField("latest_block")
    private String latestBlock;
    @TableField("avg_block_time")
    private String avgBlockTime;
    private String ttc;
    private String tps;
    @TableField("market_cap")
    private String marketCap;
    private String difficulty;
    @TableField("avg_hash_rate")
    private String avgHashRate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(String exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public String getUpDown() {
        return upDown;
    }

    public void setUpDown(String upDown) {
        this.upDown = upDown;
    }

    public String getLatestBlock() {
        return latestBlock;
    }

    public void setLatestBlock(String latestBlock) {
        this.latestBlock = latestBlock;
    }

    public String getAvgBlockTime() {
        return avgBlockTime;
    }

    public void setAvgBlockTime(String avgBlockTime) {
        this.avgBlockTime = avgBlockTime;
    }

    public String getTtc() {
        return ttc;
    }

    public void setTtc(String ttc) {
        this.ttc = ttc;
    }

    public String getTps() {
        return tps;
    }

    public void setTps(String tps) {
        this.tps = tps;
    }

    public String getMarketCap() {
        return marketCap;
    }

    public void setMarketCap(String marketCap) {
        this.marketCap = marketCap;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getAvgHashRate() {
        return avgHashRate;
    }

    public void setAvgHashRate(String avgHashRate) {
        this.avgHashRate = avgHashRate;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "Summary{" +
        "id=" + id +
        ", price=" + price +
        ", exchangeRate=" + exchangeRate +
        ", upDown=" + upDown +
        ", latestBlock=" + latestBlock +
        ", avgBlockTime=" + avgBlockTime +
        ", ttc=" + ttc +
        ", tps=" + tps +
        ", marketCap=" + marketCap +
        ", difficulty=" + difficulty +
        ", avgHashRate=" + avgHashRate +
        "}";
    }
}
