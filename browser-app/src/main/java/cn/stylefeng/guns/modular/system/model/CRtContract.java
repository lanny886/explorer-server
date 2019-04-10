package cn.stylefeng.guns.modular.system.model;

import com.baomidou.mybatisplus.enums.IdType;
import java.math.BigDecimal;
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
 * @since 2019-03-30
 */
@TableName("c_rt_contract")
public class CRtContract extends Model<CRtContract> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private String total;
    private BigDecimal decimal;
    private String name;
    private String symbol;
    private String asset;
    private String hash;
    private String blockNumer;
    private String contract;
    private String tokenStandard;
    private String tokenAction;
    private String from;
    private String to;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public BigDecimal getDecimal() {
        return decimal;
    }

    public void setDecimal(BigDecimal decimal) {
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

    public String getBlockNumer() {
        return blockNumer;
    }

    public void setBlockNumer(String blockNumer) {
        this.blockNumer = blockNumer;
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

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "CRtContract{" +
        ", id=" + id +
        ", total=" + total +
        ", decimal=" + decimal +
        ", name=" + name +
        ", symbol=" + symbol +
        ", asset=" + asset +
        ", hash=" + hash +
        ", blockNumer=" + blockNumer +
        ", contract=" + contract +
        ", tokenStandard=" + tokenStandard +
        ", tokenAction=" + tokenAction +
        ", from=" + from +
        ", to=" + to +
        "}";
    }
}
