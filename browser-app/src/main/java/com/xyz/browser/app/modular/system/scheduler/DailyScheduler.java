package com.xyz.browser.app.modular.system.scheduler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.google.common.collect.Maps;
import com.xyz.browser.app.config.properties.GunsProperties;
import com.xyz.browser.app.core.common.annotion.TimeStat;
import com.xyz.browser.app.core.redission.DistributedRedisLock;
import com.xyz.browser.app.core.redission.RedisLockConstant;
import com.xyz.browser.app.modular.hbase.model.Block;
import com.xyz.browser.app.modular.hbase.service.BlockService;
import com.xyz.browser.app.modular.hbase.service.BtransactionService;
import com.xyz.browser.app.modular.hbase.service.TransactionService;
import com.xyz.browser.app.modular.system.model.RtBlock;
import com.xyz.browser.app.modular.system.model.Summary;
import com.xyz.browser.app.modular.system.model.TxnDaily;
import com.xyz.browser.app.modular.system.service.IRtBlockService;
import com.xyz.browser.app.modular.system.service.IRtTxnService;
import com.xyz.browser.app.modular.system.service.ISummaryService;
import com.xyz.browser.app.modular.system.service.ITxnDailyService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

@Component
@ConditionalOnProperty(prefix = "guns.scheduler-switch", name = "daily", havingValue = "true")
@Slf4j
public class DailyScheduler {
    @Autowired
    private DistributedRedisLock distributedRedisLock;
    @Autowired
    private GunsProperties gunsProperties;
    @Autowired
    private DailyScheduler self;
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

    private static FastDateFormat sdf = FastDateFormat.getInstance("yyyy-MM-dd");

    //@TimeStat
    @Scheduled(cron = "5 0 5 * * *")
    public void work() {
        boolean lockflag = distributedRedisLock.tryAcquire(RedisLockConstant.DAILY,600);
        if(lockflag) {
            self.start();
        }
    }
    @TimeStat
    public void start(){
        RtBlock rtBlock = rtBlockService.selectOne(new EntityWrapper<RtBlock>().orderBy("number",false).last("limit 1"));
        if(rtBlock!=null){
            Summary summary = new Summary();
            Block block = blockService.selectByHash(rtBlock.getHash());
            if(block!=null){
                String difficulty = new BigInteger(block.getTotalDifficulty().substring(2),16).toString();
                summary.setDifficulty(difficulty);
                summary.setAvgHashRate(difficulty);
            }
            int size=5000;
            if(rtBlock.getNumber()-5000<0)
                size = rtBlock.getNumber().intValue();
            RtBlock preBlock = rtBlockService.selectOne(new EntityWrapper<RtBlock>().where("number = {0}",rtBlock.getNumber()-size));
            if(preBlock!=null){
                summary.setAvgBlockTime(String.valueOf((Long.valueOf(rtBlock.getT())-Long.valueOf(preBlock.getT()))/size));
            }

            summaryService.upt(summary);
        }

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH,-1);
        cal.set(Calendar.HOUR_OF_DAY,0);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND,0);
        cal.set(Calendar.MILLISECOND,0);
        String flag = sdf.format(cal.getTime());
        String hexStart = "0x"+Integer.toHexString((int)(cal.getTimeInMillis()/1000));
        cal.set(Calendar.HOUR_OF_DAY,23);
        cal.set(Calendar.MINUTE,59);
        cal.set(Calendar.SECOND,59);
        cal.set(Calendar.MILLISECOND,999);
        String hexEnd = "0x"+Integer.toHexString((int)(cal.getTimeInMillis()/1000));
        Map<String,Object> params = Maps.newHashMap();
        params.put("hexStart",hexStart);
        params.put("hexEnd",hexEnd);
        Long count = btransactionService.dailyCount(params);
        TxnDaily txnDaily = new TxnDaily();
        txnDaily.setDay(flag);
        txnDaily.setAmount(String.valueOf(count));
        txnDailyService.insertOrUpdate(txnDaily);
    }
}
