package com.xyz.browser.app.modular.hbase.model;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("block_sync")
public class BlockSync extends Model<BlockSync> {
    @TableId(value = "hash")
    private String hash;

    @Override
    protected Serializable pkVal() {
        return this.hash;
    }
}
