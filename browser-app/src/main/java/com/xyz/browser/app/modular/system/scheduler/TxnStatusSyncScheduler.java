package com.xyz.browser.app.modular.system.scheduler;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.xyz.browser.app.config.properties.GunsProperties;
import com.xyz.browser.app.core.common.annotion.TimeStat;
import com.xyz.browser.app.core.redission.DistributedRedisLock;
import com.xyz.browser.app.core.redission.RedisLockConstant;
import com.xyz.browser.app.modular.hbase.model.Block;
import com.xyz.browser.app.modular.hbase.model.BlockSync;
import com.xyz.browser.app.modular.hbase.model.Transaction;
import com.xyz.browser.app.modular.hbase.model.TxnStatusSync;
import com.xyz.browser.app.modular.hbase.service.*;
import com.xyz.browser.app.modular.system.model.RtBlock;
import com.xyz.browser.app.modular.system.model.RtTxn;
import com.xyz.browser.app.modular.system.service.IRtBlockService;
import com.xyz.browser.app.modular.system.service.IRtTxnService;
import com.xyz.browser.app.modular.system.service.ISummaryService;
import com.xyz.browser.app.modular.system.service.ITxnDailyService;
import com.xyz.browser.common.constant.Reward;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Component
@ConditionalOnProperty(prefix = "guns.scheduler-switch", name = "txnStatusSync", havingValue = "true")
@Slf4j
public class TxnStatusSyncScheduler {
    @Autowired
    private DistributedRedisLock distributedRedisLock;
    @Autowired
    private GunsProperties gunsProperties;
    @Autowired
    private TxnStatusSyncScheduler self;
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
    private TxnStatusSyncService txnStatusSyncService;

    private static FastDateFormat sdf = FastDateFormat.getInstance("yyyy-MM-dd");

    private int checkSize=1000;


    //@TimeStat
    @Scheduled(cron = "30 0/1 * * * *")
    public void work() {
        boolean lockflag = distributedRedisLock.tryAcquire(RedisLockConstant.TXN_STATUS_SYNC,20);

        if(lockflag) {
            self.start();
    }
    }
    @TimeStat
    public void start(){
//        List<RtBlock> rtBlockList = rtBlockService.selectList(new EntityWrapper<RtBlock>().setSqlSelect("number").orderBy("number",false).last("limit "+checkSize));

        List<TxnStatusSync> txnStatusSyncs = txnStatusSyncService.selectAll();
        if(CollUtil.isNotEmpty(txnStatusSyncs) ){
            for(TxnStatusSync txnStatusSync:txnStatusSyncs){
                String hash = txnStatusSync.getTransactionHash();
                Transaction transaction = transactionService.selectByHash(hash);
                if(transaction!=null){
                    RtTxn rtTxn = new RtTxn();
                    rtTxn.setHash(transaction.getTransactionHash());
                    rtTxn.setStatus(Integer.valueOf(transaction.getStatus().substring(2),16).toString());
                    rtTxnService.updateStatus(rtTxn);
                    txnStatusSyncService.deleteByHash(hash);
                }
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
