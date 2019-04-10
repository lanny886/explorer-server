/**
 * Copyright 2018-2020 stylefeng & fengshuonan (https://gitee.com/stylefeng)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xyz.browser.app.modular.system.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.xyz.browser.app.config.properties.GunsProperties;
import com.xyz.browser.app.modular.system.dao.UserMapper;
import com.xyz.browser.app.modular.system.model.OperationLog;
import com.xyz.browser.app.modular.system.model.User;
import com.xyz.browser.app.modular.system.service.IUserService;
import com.xyz.browser.app.core.sms.*;
import cn.stylefeng.roses.core.datascope.DataScope;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 管理员表 服务实现类
 * </p>
 *
 * @author stylefeng123
 * @since 2018-02-22
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private SmsMessageHandler smsMessageHandler;

    @Autowired
    private SmsProperties smsProperties;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public int setStatus(Integer userId, int status) {
        return this.baseMapper.setStatus(userId, status);
    }

    @Override
    public int changePwd(Integer userId, String pwd) {
        return this.baseMapper.changePwd(userId, pwd);
    }

    @Override
    public List<Map<String, Object>> selectUsers(DataScope dataScope, String name, String beginTime, String endTime, Integer deptid) {
        return this.baseMapper.selectUsers(dataScope, name, beginTime, endTime, deptid);
    }
    @Override
    public List<Map<String, Object>> selectPageUsers(Page<User> page, DataScope dataScope, String name, String beginTime, String endTime, Integer deptid) {
        return this.baseMapper.selectUsers(page,dataScope, name, beginTime, endTime, deptid);
    }

    @Override
    public int setRoles(Integer userId, String roleIds) {
        return this.baseMapper.setRoles(userId, roleIds);
    }

    @Override
    public User getByAccount(String account) {
        return this.baseMapper.getByAccount(account);
    }

    public User getByPhone(String phone){
        return this.baseMapper.getByPhone(phone);
    }

    @Override
    public boolean sendSmsCode(SmsChannelTemplateEnum enumSmsChannelTemplate, String mobile) {
        Object obj = redisTemplate.opsForValue().get(CommonConstant.DEFAULT_CODE_COUNT+mobile);
        int count = (obj!=null)?(int)obj:0;
        if(count >= 1)
            return false;
        String code = RandomUtil.randomNumbers(4);
        JSONObject contextJson = new JSONObject();
        contextJson.put("code", code);
        System.out.println("短信发送请求消息中心 -> 手机号:{} -> 验证码：{}"+ mobile+","+code);
        smsMessageHandler.execute(new MobileMsgTemplate(
                mobile,
                contextJson.toJSONString(),
                smsProperties.getSignName(),
                enumSmsChannelTemplate.getTemplate()
        ));
        redisTemplate.opsForValue().set(CommonConstant.DEFAULT_CODE_KEY+mobile,code,CommonConstant.DEFAULT_EXPIRE, TimeUnit.SECONDS);
        redisTemplate.opsForValue().increment(CommonConstant.DEFAULT_CODE_COUNT+mobile,1);
        redisTemplate.expire(CommonConstant.DEFAULT_CODE_COUNT+mobile,CommonConstant.CODE_COUNT_EXPIRE,TimeUnit.SECONDS);
        return true;
    }

}
