package com.xyz.browser.app.modular.system.scheduler;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.entity.Columns;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.alibaba.fastjson.JSONObject;
import com.xyz.browser.app.config.properties.GunsProperties;
import com.xyz.browser.app.core.common.annotion.TimeStat;
import com.xyz.browser.app.core.redission.DistributedRedisLock;
import com.xyz.browser.app.core.redission.RedisLockConstant;
import com.xyz.browser.app.modular.hbase.model.Block;
import com.xyz.browser.app.modular.hbase.model.BlockSync;
import com.xyz.browser.app.modular.hbase.model.Transaction;
import com.xyz.browser.app.modular.hbase.service.BlockService;
import com.xyz.browser.app.modular.hbase.service.BlockSyncService;
import com.xyz.browser.app.modular.hbase.service.BtransactionService;
import com.xyz.browser.app.modular.hbase.service.TransactionService;
import com.xyz.browser.app.modular.system.model.RtBlock;
import com.xyz.browser.app.modular.system.model.RtTxn;
import com.xyz.browser.app.modular.system.model.Summary;
import com.xyz.browser.app.modular.system.model.TxnDaily;
import com.xyz.browser.app.modular.system.service.IRtBlockService;
import com.xyz.browser.app.modular.system.service.IRtTxnService;
import com.xyz.browser.app.modular.system.service.ISummaryService;
import com.xyz.browser.app.modular.system.service.ITxnDailyService;
import com.xyz.browser.common.constant.Reward;
import lombok.experimental.var;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@ConditionalOnProperty(prefix = "guns.scheduler-switch", name = "blockSync", havingValue = "true")
@Slf4j
public class BlockSyncScheduler {
    @Autowired
    private DistributedRedisLock distributedRedisLock;
    @Autowired
    private GunsProperties gunsProperties;
    @Autowired
    private BlockSyncScheduler self;
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


    //@TimeStat
    @Scheduled(cron = "30 0/1 * * * *")
    public void work() {
        boolean lockflag = distributedRedisLock.tryAcquire(RedisLockConstant.BLOCK_SYNC,20);

        if(lockflag) {
            self.start();
        }
    }
    @TimeStat
    public void start(){
//        List<RtBlock> rtBlockList = rtBlockService.selectList(new EntityWrapper<RtBlock>().setSqlSelect("number").orderBy("number",false).last("limit "+checkSize));

        List<BlockSync> blockSyncs = blockSyncService.selectAll();
        if(CollUtil.isNotEmpty(blockSyncs) ){
            for(BlockSync blockSync:blockSyncs){
                String hash = blockSync.getHash();
                Block block = blockService.selectByHash(hash);
                if(block!=null){
                    RtBlock rtBlock1=new RtBlock();
                    rtBlock1.setNumber(Long.valueOf(block.getNumber().substring(2),16));
                    rtBlock1.setMiner(block.getMiner());
                    rtBlock1.setReward(Reward.BLOCK_REWARD);
                    String timestamp = Long.valueOf(block.getTimestamp().substring(2),16).toString();
                    rtBlock1.setT(timestamp);
                    JSONArray txnArr = JSON.parseArray(block.getTransactions());
                    rtBlock1.setTxnCount(String.valueOf(txnArr.size()));
                    List<String> arrUnc = JSON.parseArray(block.getUncles(),String.class);
                    rtBlock1.setUncleCount(String.valueOf(arrUnc.size()));
                    rtBlock1.setGasUsed(new BigInteger(block.getGasUsed().substring(2),16).toString());
                    rtBlock1.setGasLimit(new BigInteger(block.getGasLimit().substring(2),16).toString());
                    BigDecimal totalGasPrice =new BigDecimal("0.0");

                    List<RtTxn> rtTxns = Lists.newArrayList();
                    for(int k=0;k<txnArr.size();k++){
                        JSONObject obj = txnArr.getJSONObject(k);

                        String txnHash = obj.getString("hash");
//                            RtTxn rtTxn1 = rtTxnService.selectById(txnHash);
                        RtTxn rtTxn = null;
                        rtTxn = new RtTxn();
                        rtTxn.setHash(txnHash);
                        rtTxn.setBlockNumber(Long.valueOf(obj.getString("blockNumber").substring(2),16));
                        rtTxn.setFrom(obj.getString("from"));
                        rtTxn.setTo(obj.getString("to"));

                        val gas = new java.math.BigInteger(obj.getString("gas").substring(2),16);
                        val gasPrice = new java.math.BigInteger(obj.getString("gasPrice").substring(2),16);
                        val txnFee = new BigDecimal(gas.multiply(gasPrice))
                                .divide(new java.math.BigDecimal("1000000000000000000")).toString();
                        rtTxn.setTxnFee(txnFee);

                        String value = new java.math.BigDecimal(new java.math.BigInteger(obj.getString("value").substring(2),16).toString())
                                .divide(new java.math.BigDecimal("1000000000000000000")).toString();
                        rtTxn.setValue(value);
                        rtTxn.setT(timestamp);
//                            rtTxn.setStatus("");
                        rtTxn.setBlockHash(obj.getString("blockHash"));
//                        Transaction transaction = transactionService.selectByHash(txnHash);
//                        if(transaction!=null){
//                            rtTxn.setStatus(Integer.valueOf(transaction.getStatus().substring(2),16).toString());
//                        }
                        rtTxns.add(rtTxn);

                        totalGasPrice = totalGasPrice.add(new BigDecimal(gasPrice));
                    }
                    String avgGasPrice ="0.0";
                    if(txnArr.size() > 0)
                        avgGasPrice = totalGasPrice.divide(new BigDecimal(String.valueOf(txnArr.size())),java.math.BigDecimal.ROUND_HALF_UP).toString();

                    rtBlock1.setAvgGasPrice(avgGasPrice);
                    rtBlock1.setHash(block.getHash());

                    rtBlockService.insertRtData(rtBlock1,rtTxns,arrUnc);//.insertOrUpdate(rtBlock1);
                    blockSyncService.deleteByHash(hash);
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
