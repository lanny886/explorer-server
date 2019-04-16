package com.xyz.browser.app.modular.hbase.model;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("bancor")
public class Bancor extends Model<Bancor> {

    @TableField("hash")
    private String hash;
    @TableField("tfrom")
    private String tfrom;

    @TableField("contract")
    private String contract;

    @TableField("action")
    private String action;

    @TableField("ttype")
    private String ttype;

    @TableField("name")
    private String name;

    @TableField("tfunction")
    private String tfunction;

    @TableField("param")
    private JSONArray param;

    @TableField("input")
    private String input;

    @Override
    protected Serializable pkVal() {
        return this.hash;
    }
}
