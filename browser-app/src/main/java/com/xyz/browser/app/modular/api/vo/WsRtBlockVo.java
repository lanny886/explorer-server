package com.xyz.browser.app.modular.api.vo;

import com.xyz.browser.common.model.RtTxnDto;
import lombok.Data;

import java.util.List;
@Data
public class WsRtBlockVo {
    private Long number;
    private String miner;
    private List<WsRtTxnVo> txns;
    private String reward;
    private String t;

    private String interval;
}
