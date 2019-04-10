package com.xyz.browser.app.modular.system.service.impl;

import com.xyz.browser.app.modular.system.model.TimeLog;
import com.xyz.browser.app.modular.system.dao.TimeLogMapper;
import com.xyz.browser.app.modular.system.service.ITimeLogService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 时间统计日志表 服务实现类
 * </p>
 *
 * @author stylefeng
 * @since 2018-11-30
 */
@Service
public class TimeLogServiceImpl extends ServiceImpl<TimeLogMapper, TimeLog> implements ITimeLogService {

}
