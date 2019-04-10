package com.xyz.browser.app.modular.api.vo;

import com.baomidou.mybatisplus.activerecord.Model;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class TransactionVo implements Serializable {
    private String hash;
    private String status;
    private String blockHash;
    private String blockNumber;
    private String timestamp;
    private String from;
    private String to;
    private String value;
    private String txnFee;
    private String gasLimit;
    private String gasUsed;
    private String gasPrice;
    private String nonce;
    private String position;
    private String inputData;
    private List<LogVo> logs;//jsonStr arr

    private String confirms;
    private String contractAddress;

}
