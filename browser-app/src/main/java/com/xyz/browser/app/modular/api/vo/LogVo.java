package com.xyz.browser.app.modular.api.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class LogVo implements Serializable {
//    private String blockHash;
    private String address;
    private String logIndex;
    private String data;
    private Boolean removed;
    private List<String> topics;
//    private String blockNumber;
//    private String tnxIndex;
//    private String tnxHash;

}
