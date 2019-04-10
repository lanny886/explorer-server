package com.xyz.browser.app.modular.hbase.model;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("transaction_sync")
public class TxnStatusSync extends Model<TxnStatusSync> {
    @TableId(value = "transactionHash")
    private String transactionHash;

    @Override
    protected Serializable pkVal() {
        return this.transactionHash;
    }
}
