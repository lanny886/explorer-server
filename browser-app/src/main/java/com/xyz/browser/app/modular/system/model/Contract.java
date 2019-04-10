package com.xyz.browser.app.modular.system.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;

@TableName("c_rt_contract")
public class Contract {

    private String total;

    private String decimal;

    private String name;

    private String symbol;

    private String asset;

    private String hash;

    @TableField("block_number")
    private String blockNumber;

    private String contract;

    @TableField("token_standard")
    private String tokenStandard;

    @TableField("token_action")
    private String tokenAction;

    @TableField("from")
    private String tfrom;

    @TableField("to")
    private String tto;

    private String holders;

    private String quantity;

    private Long t;

    public Long getT() {
        return t;
    }

    public void setT(Long t) {
        this.t = t;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getHolders() {
        return holders;
    }

    public void setHolders(String holders) {
        this.holders = holders;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getDecimal() {
        return decimal;
    }

    public void setDecimal(String decimal) {
        this.decimal = decimal;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getAsset() {
        return asset;
    }

    public void setAsset(String asset) {
        this.asset = asset;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(String blockNumber) {
        this.blockNumber = blockNumber;
    }

    public String getContract() {
        return contract;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }

    public String getTokenStandard() {
        return tokenStandard;
    }

    public void setTokenStandard(String tokenStandard) {
        this.tokenStandard = tokenStandard;
    }

    public String getTokenAction() {
        return tokenAction;
    }

    public void setTokenAction(String tokenAction) {
        this.tokenAction = tokenAction;
    }

    public String getTfrom() {
        return tfrom;
    }

    public void setTfrom(String tfrom) {
        this.tfrom = tfrom;
    }

    public String getTto() {
        return tto;
    }

    public void setTto(String tto) {
        this.tto = tto;
    }


}
