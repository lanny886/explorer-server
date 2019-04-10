package com.xyz.browser.app.modular.system.service;

import com.xyz.browser.app.modular.system.model.RtBlock;
import com.baomidou.mybatisplus.service.IService;
import com.xyz.browser.app.modular.system.model.RtTxn;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 实时block 服务类
 * </p>
 *
 * @author stylefeng
 * @since 2019-03-18
 */
public interface IRtBlockService extends IService<RtBlock> {

    void insertRtData(RtBlock rtBlock, List<RtTxn> rtTxnList,List<String> uncles);

    List<RtBlock> pageList(Map<String,Object> params);

    long pageCount(Map<String, Object> params);
}
