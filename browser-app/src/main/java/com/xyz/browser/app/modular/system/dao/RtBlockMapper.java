package com.xyz.browser.app.modular.system.dao;

import com.xyz.browser.app.modular.system.model.RtBlock;
import com.baomidou.mybatisplus.mapper.BaseMapper;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 实时block Mapper 接口
 * </p>
 *
 * @author stylefeng
 * @since 2019-03-18
 */
public interface RtBlockMapper extends BaseMapper<RtBlock> {

    List<RtBlock> pageList(Map<String, Object> params);

    long pageCount(Map<String, Object> params);
}
