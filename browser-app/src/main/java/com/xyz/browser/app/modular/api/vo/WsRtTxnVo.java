package com.xyz.browser.app.modular.api.vo;

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
public class WsRtTxnVo{
    private String hash;
    private Long blockNumber;
    private String from;
    private String to;
    private String value;
    private String t;

}
