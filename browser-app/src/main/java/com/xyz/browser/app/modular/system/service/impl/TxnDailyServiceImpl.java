package com.xyz.browser.app.modular.system.service.impl;

import com.xyz.browser.app.modular.system.model.TxnDaily;
import com.xyz.browser.app.modular.system.dao.TxnDailyMapper;
import com.xyz.browser.app.modular.system.service.ITxnDailyService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * transaction每天数量 服务实现类
 * </p>
 *
 * @author stylefeng
 * @since 2019-03-18
 */
@Service
public class TxnDailyServiceImpl extends ServiceImpl<TxnDailyMapper, TxnDaily> implements ITxnDailyService {

}
