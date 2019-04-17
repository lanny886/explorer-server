package com.xyz.browser.app.modular.api.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressVo {
    private String balance;
    private String txnCount;
    private String minedBlocks;
    private String minedUncles;
    private String volume;
    private Integer rank;
//    private List<AddressLatestTxnVo> latestTxns;
}