package com.xyz.browser.app.modular.system.dao;

import com.xyz.browser.app.modular.system.model.RankingAsset;
import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author stylefeng
 * @since 2019-04-10
 */
public interface RankingAssetMapper extends BaseMapper<RankingAsset> {

    Integer selectRankByAddress(String address);

}
