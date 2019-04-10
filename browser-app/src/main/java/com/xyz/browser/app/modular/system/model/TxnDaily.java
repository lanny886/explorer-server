package com.xyz.browser.app.modular.system.model;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;

/**
 * <p>
 * transaction每天数量
 * </p>
 *
 * @author stylefeng
 * @since 2019-03-18
 */
@TableName("s_txn_daily")
public class TxnDaily extends Model<TxnDaily> {

    private static final long serialVersionUID = 1L;
    @TableId("day")
    private String day;
    private String amount;
    private String price;


    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @Override
    protected Serializable pkVal() {
        return this.day;
    }

    @Override
    public String toString() {
        return "TxnDaily{" +
        ", day=" + day +
        ", amount=" + amount +
        ", price=" + price +
        "}";
    }
}
