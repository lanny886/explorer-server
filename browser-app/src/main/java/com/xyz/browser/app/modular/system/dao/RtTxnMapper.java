package com.xyz.browser.app.modular.system.dao;

import com.xyz.browser.app.modular.system.model.RtTxn;
import com.baomidou.mybatisplus.mapper.BaseMapper;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 实时transaction Mapper 接口
 * </p>
 *
 * @author stylefeng
 * @since 2019-03-18
 */
public interface RtTxnMapper extends BaseMapper<RtTxn> {

    List<RtTxn> pageList(Map<String, Object> params);

    long pageCount(Map<String, Object> params);

    RtTxn out(String address);

    RtTxn in(String address);

    List<String> selectListByAddress(String address);
}
