package com.xyz.browser.app.modular.hbase.model;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("address_transaction")
public class AddressTransaction extends Model<AddressTransaction> {

    @TableField("address")
    private String address;
    @TableField("volume")
    private String volume;

    @Override
    protected Serializable pkVal() {
        return this.address;
    }


}
