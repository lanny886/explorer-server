package com.xyz.browser.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 实时block
 * </p>
 *
 * @author stylefeng
 * @since 2019-03-18
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RtBlockDto implements Serializable{

    private static final long serialVersionUID = 1L;

    private Long number;

    private String miner;
    private List<RtTxnDto> txns;
    private String reward;
    private String t;

//    private String uncleCount;

    private List<String> uncles;

    private String gasUsed;
    private String gasLimit;
    private String avgGasPrice;

    private String hash;


}
