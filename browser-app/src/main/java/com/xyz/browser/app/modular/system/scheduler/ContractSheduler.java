package com.xyz.browser.app.modular.system.scheduler;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.xyz.browser.app.core.common.annotion.TimeStat;
import com.xyz.browser.app.core.redission.DistributedRedisLock;
import com.xyz.browser.app.core.redission.RedisLockConstant;
import com.xyz.browser.app.modular.hbase.model.Bancor;
import com.xyz.browser.app.modular.hbase.model.Block;
import com.xyz.browser.app.modular.hbase.model.BlockSync;
import com.xyz.browser.app.modular.hbase.service.BancorService;
import com.xyz.browser.app.modular.system.model.Contract;
import com.xyz.browser.app.modular.system.model.RtBlock;
import com.xyz.browser.app.modular.system.model.RtTxn;
import com.xyz.browser.app.modular.system.service.IContractService;
import com.xyz.browser.common.constant.Reward;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Component
@ConditionalOnProperty(prefix = "guns.scheduler-switch", name = "contract", havingValue = "true")
@Slf4j
public class ContractSheduler {

    @Autowired
    private DistributedRedisLock distributedRedisLock;

    @Autowired
    private ContractSheduler self;

    @Autowired
    private IContractService iContractService;

    @Autowired
    private BancorService bancorService;

    @Scheduled(cron = "30 0/1 * * * *")
    public void work() {
        boolean lockflag = distributedRedisLock.tryAcquire(RedisLockConstant.BLOCK_SYNC,20);

        if(lockflag) {
            self.start();
        }
    }
    @TimeStat
    public void start(){

        try {
            List<Bancor> bancorList = bancorService.selectBanCor("smartToken");

            for (Bancor bancor : bancorList) {

                String total = iContractService.getTotal(bancor.getContract());

                Contract contract = new Contract();
                contract.setTokenAction("create");
                contract.setContract(bancor.getContract());
                contract.setTotal(total);
                iContractService.updateContract(contract);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


}
