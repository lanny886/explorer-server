package com.xyz.browser.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContractDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String hash;

    private String total;

    private String decimal;

    private String name;

    private String symbol;

    private String asset;

    private String blockNumber;

    private String contract;

    private String tokenStandard;

    private String tokenAction;

    private String tfrom;

    private String tto;

}
