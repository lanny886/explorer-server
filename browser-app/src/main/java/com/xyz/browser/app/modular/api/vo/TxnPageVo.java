package com.xyz.browser.app.modular.api.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TxnPageVo {
    private String hash;
    private String blockNumber;
    private String timestamp;
    private String from;
    private String to;
    private String value;
    private String txnFee;
    private String status;

    private String io;
}
