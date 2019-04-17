package com.xyz.browser.app.modular.system.service;

import com.xyz.browser.app.modular.system.model.RankingAsset;
import com.baomidou.mybatisplus.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author stylefeng
 * @since 2019-04-10
 */
public interface IRankingAssetService extends IService<RankingAsset> {

    Integer selectRankByAddress(String address);

}
