package com.xyz.browser.app.modular.system.scheduler;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.xyz.browser.app.config.properties.GunsProperties;
import com.xyz.browser.app.core.common.annotion.TimeStat;
import com.xyz.browser.app.core.redission.DistributedRedisLock;
import com.xyz.browser.app.core.redission.RedisLockConstant;
import com.xyz.browser.app.modular.hbase.service.BtransactionService;
import com.xyz.browser.app.modular.hbase.service.TransactionService;
import com.xyz.browser.app.modular.system.model.RtTxn;
import com.xyz.browser.app.modular.system.model.Summary;
import com.xyz.browser.app.modular.system.service.IRtTxnService;
import com.xyz.browser.app.modular.system.service.ISummaryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConditionalOnProperty(prefix = "guns.scheduler-switch", name = "txnTotalCount", havingValue = "true")
@Slf4j
public class TxnTotalCountScheduler {
    @Autowired
    private DistributedRedisLock distributedRedisLock;
    @Autowired
    private GunsProperties gunsProperties;
    @Autowired
    private TxnTotalCountScheduler self;
    @Autowired
    private IRtTxnService rtTxnService;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private BtransactionService btransactionService;
    @Autowired
    private ISummaryService summaryService;

    private static FastDateFormat sdf = FastDateFormat.getInstance("yyyyMMdd");

    //@TimeStat
    @Scheduled(cron = "5 0/5 * * * *")
    public void work() {
        boolean lockflag = distributedRedisLock.tryAcquire(RedisLockConstant.TXN_TOTAL_COUNT,60);
        if(lockflag) {
            self.start();
        }
    }
    @TimeStat
    public void start(){
        Long totalCount = btransactionService.totalCount();
        Summary summary = new Summary();
        summary.setTtc(totalCount.toString());
        summaryService.upt(summary);


    }

}
