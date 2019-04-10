package com.xyz.browser.app.modular.hbase.model;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("contract")
public class Contract extends Model<Contract>{

    @TableId("hash")
    private String hash;

    @TableId("total")
    private String total;

    @TableId("decimal")
    private String decimal;

    @TableId("name")
    private String name;

    @TableId("symbol")
    private String symbol;

    @TableId("asset")
    private String asset;

    @TableId("blockNumber")
    private String blockNumber;

    @TableId("contract")
    private String contract;

    @TableId("tokenStandard")
    private String tokenStandard;

    @TableId("tokenAction")
    private String tokenAction;

    @TableId("tfrom")
    private String tfrom;

    @TableId("tto")
    private String tto;

    @Override
    protected Serializable pkVal() {
        return this.hash;
    }
}
