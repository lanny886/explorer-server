package com.xyz.browser.app.modular.api.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeptVo implements Serializable {
    /*
    门店id
     */
    private Integer id;
    /*
    门店名称
     */
    private String name;
    /*
    门店图片
     */
    private String avatar;

}
