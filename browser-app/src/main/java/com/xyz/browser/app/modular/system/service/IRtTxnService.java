package com.xyz.browser.app.modular.system.service;

import com.xyz.browser.app.modular.system.model.RtTxn;
import com.baomidou.mybatisplus.service.IService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 实时transaction 服务类
 * </p>
 *
 * @author stylefeng
 * @since 2019-03-18
 */
public interface IRtTxnService extends IService<RtTxn> {

    List<RtTxn> pageList(Map<String,Object> params);

    long pageCount(Map<String, Object> params);

    BigDecimal balance(String address);

    void updateStatus(RtTxn rtTxn);

    String balanceFromOut(String address);
}
