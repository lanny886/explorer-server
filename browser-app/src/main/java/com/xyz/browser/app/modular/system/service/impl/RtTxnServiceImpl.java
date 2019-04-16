package com.xyz.browser.app.modular.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xyz.browser.app.core.common.constant.cache.Cache;
import com.xyz.browser.app.core.common.constant.cache.CacheKey;
import com.xyz.browser.app.core.redission.DistributedRedisLock;
import com.xyz.browser.app.core.redission.RedisLockConstant;
import com.xyz.browser.app.core.sms.CommonConstant;
import com.xyz.browser.app.modular.system.model.RtTxn;
import com.xyz.browser.app.modular.system.dao.RtTxnMapper;
import com.xyz.browser.app.modular.system.service.IRtTxnService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 实时transaction 服务实现类
 * </p>
 *
 * @author stylefeng
 * @since 2019-03-18
 */
@Service
public class RtTxnServiceImpl extends ServiceImpl<RtTxnMapper, RtTxn> implements IRtTxnService {

    @Autowired
    private RtTxnMapper rtTxnMapper;
    @Autowired
    private DistributedRedisLock distributedRedisLock;
    @Override
    public List<RtTxn> pageList(Map<String, Object> params) {
        return rtTxnMapper.pageList(params);
    }
    @Value("${guns.inf.balance}")
    private String balanceInf;

    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public long pageCount(Map<String, Object> params) {
        return rtTxnMapper.pageCount(params);
    }

    @Override
    public BigDecimal balance(String address) {
        RtTxn out = rtTxnMapper.out(address);
        RtTxn in = rtTxnMapper.in(address);
        BigDecimal ov = BigDecimal.ZERO;
        BigDecimal of = BigDecimal.ZERO;
        if(out!=null){
            if(StringUtils.isNotBlank(out.getValue()))
                ov = new BigDecimal(out.getValue());
            if(StringUtils.isNotBlank(out.getTxnFee()))
                of = new BigDecimal(out.getTxnFee());
        }
        BigDecimal iv = BigDecimal.ZERO;
        if(in!=null && StringUtils.isNotBlank(in.getValue())){
            iv = new BigDecimal(in.getValue());
        }
        return iv.subtract(ov.add(of));
    }

    @Override
    public String balanceFromOut(String address) {
        String balance = (String)redisTemplate.opsForValue().get(CacheKey.KEY_ADDRESS_BALANCE+address);
        if(StringUtils.isNotBlank(balance))
            return balance;
        HttpRequest post = HttpUtil.createPost(balanceInf);
        post.contentType("text/plain");
        post.body("{\"coin\":\"VNS\",\"address\":\"" + address + "\"}");
        HttpResponse response = post.execute();
        if (response.getStatus() == 200) {
            JSONObject obj = JSON.parseObject(response.body());
            balance = obj.getString("data");
            redisTemplate.opsForValue().set(CacheKey.KEY_ADDRESS_BALANCE+address,balance,10, TimeUnit.SECONDS);
            return balance;
        }
        return null;
    }

    @Override
    public List<String> selectListByAddress(String address) {
        return rtTxnMapper.selectListByAddress(address);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(RtTxn rtTxn) {
        try {
            distributedRedisLock.acquire(RedisLockConstant.RT_TXN+"-"+rtTxn.getHash());
            RtTxn check = rtTxnMapper.selectById(rtTxn.getHash());
            if (check == null)
                rtTxnMapper.insert(rtTxn);
            else {
                BeanUtil.copyProperties(rtTxn, check, new CopyOptions().setIgnoreNullValue(true));
                rtTxnMapper.updateById(check);
            }
        }finally{
            distributedRedisLock.release(RedisLockConstant.RT_TXN+"-"+rtTxn.getHash());
        }
    }
}
