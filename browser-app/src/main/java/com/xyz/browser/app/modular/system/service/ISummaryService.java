package com.xyz.browser.app.modular.system.service;

import com.xyz.browser.app.modular.system.model.Summary;
import com.baomidou.mybatisplus.service.IService;

/**
 * <p>
 * 概要信息 服务类
 * </p>
 *
 * @author stylefeng
 * @since 2019-03-18
 */
public interface ISummaryService extends IService<Summary> {

    void upt(Summary summary);
}
