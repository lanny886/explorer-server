package com.xyz.browser.app.modular.system.scheduler;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.xyz.browser.app.config.properties.GunsProperties;
import com.xyz.browser.app.core.common.annotion.TimeStat;
import com.xyz.browser.app.core.common.constant.cache.CacheKey;
import com.xyz.browser.app.core.mail.MailService;
import com.xyz.browser.app.core.redission.DistributedRedisLock;
import com.xyz.browser.app.core.redission.RedisLockConstant;
import com.xyz.browser.app.core.sms.CommonConstant;
import com.xyz.browser.app.modular.hbase.model.Block;
import com.xyz.browser.app.modular.hbase.model.BlockSync;
import com.xyz.browser.app.modular.hbase.service.BlockService;
import com.xyz.browser.app.modular.hbase.service.BlockSyncService;
import com.xyz.browser.app.modular.hbase.service.BtransactionService;
import com.xyz.browser.app.modular.hbase.service.TransactionService;
import com.xyz.browser.app.modular.system.model.RtBlock;
import com.xyz.browser.app.modular.system.model.RtTxn;
import com.xyz.browser.app.modular.system.service.IRtBlockService;
import com.xyz.browser.app.modular.system.service.IRtTxnService;
import com.xyz.browser.app.modular.system.service.ISummaryService;
import com.xyz.browser.app.modular.system.service.ITxnDailyService;
import com.xyz.browser.common.constant.Reward;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
@ConditionalOnProperty(prefix = "guns.scheduler-switch", name = "keepAlive", havingValue = "true")
@Slf4j
public class KeepAliveScheduler {
    @Autowired
    private DistributedRedisLock distributedRedisLock;
    @Autowired
    private GunsProperties gunsProperties;
    @Autowired
    private KeepAliveScheduler self;
    @Autowired
    private IRtBlockService rtBlockService;
    @Autowired
    private IRtTxnService rtTxnService;
    @Autowired
    private BlockService blockService;
    @Autowired
    private BtransactionService btransactionService;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private ISummaryService summaryService;
    @Autowired
    private ITxnDailyService txnDailyService;

    @Autowired
    private BlockSyncService blockSyncService;

    private static FastDateFormat sdf = FastDateFormat.getInstance("yyyy-MM-dd");

    private int checkSize=1000;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private MailService mailService;
    //@TimeStat
    @Scheduled(cron = "10 0/1 * * * *")
    public void work() {
        self.start();
    }
    @TimeStat
    public void start(){
        RtBlock rtBlock = rtBlockService.selectOne(new EntityWrapper<RtBlock>().orderBy("number",false).last("limit 1"));
        if(rtBlock!=null){
            long t = Long.valueOf(rtBlock.getT()+"000");
            long now = System.currentTimeMillis();
            if ((now - t)/(1000*60) > 10){
                String status = (String)redisTemplate.opsForValue().get(CacheKey.KEY_KEEP_ALIVE);
                if(StringUtils.isBlank(status) || "1".equals(status)){
                    redisTemplate.opsForValue().set(CacheKey.KEY_KEEP_ALIVE,"0",1, TimeUnit.DAYS);
                    //notify
                    mailService.send("lanny886@139.com",MailService.SUBJECT_KEEP_ALIVE,"lastBlock:"+rtBlock.getNumber()+" t:"+t);
                }
            }else{
                redisTemplate.opsForValue().set(CacheKey.KEY_KEEP_ALIVE,"1");
            }
        }

    }

    public static void main(String[] args) {
        Long maxNumber = 100L;
//            Long bottomNumber = maxNumber-checkSize+1;
//            if(bottomNumber<0) bottomNumber = 0L;
//            Long[] arr = new Long[Integer.valueOf(checkSize)];
        Set<Long> sets = Sets.newHashSet();
        for(int i=0;i<10;i++)
            sets.add(maxNumber-i);
        if(!sets.contains(maxNumber)) {
            System.out.println("none");
        }
    }
}
