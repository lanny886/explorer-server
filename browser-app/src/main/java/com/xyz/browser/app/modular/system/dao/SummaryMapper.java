package com.xyz.browser.app.modular.system.dao;

import com.xyz.browser.app.modular.system.model.Summary;
import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * <p>
 * 概要信息 Mapper 接口
 * </p>
 *
 * @author stylefeng
 * @since 2019-03-18
 */
public interface SummaryMapper extends BaseMapper<Summary> {

    void upt(Summary summary);
}
