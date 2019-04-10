package com.xyz.browser.app.modular.api.dto;

import lombok.Data;

@Data
public class TxnPageDto extends PageDto{
    private String bh;//block hash
    private String address;
    private String status;//状态（comp，pend，fail）
    private String io;//方向（out，in）
}
