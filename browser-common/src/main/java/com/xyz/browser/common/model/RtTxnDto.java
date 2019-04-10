package com.xyz.browser.common.model;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 实时transaction
 * </p>
 *
 * @author stylefeng
 * @since 2019-03-18
 */
@Data
public class RtTxnDto implements Serializable{

    private static final long serialVersionUID = 1L;

    private String hash;
    private Long blockNumber;
    private String from;
    private String to;
    private String value;
    private String t;

    private String txnFee;
    private String status;

    private String blockHash;

}
