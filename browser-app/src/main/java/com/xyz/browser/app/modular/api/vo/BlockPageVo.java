package com.xyz.browser.app.modular.api.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlockPageVo {
    private String number;
    private String timestamp;
    private String txn;
    private String uncles;
    private String miner;
    private String gasUsed;
    private String gasLimit;
    private String avgGasPrice;
    private String reward;
    private String hash;
}
