package com.xyz.browser.app.modular.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollUtil;
import com.xyz.browser.app.core.redission.DistributedRedisLock;
import com.xyz.browser.app.core.redission.RedisLockConstant;
import com.xyz.browser.app.modular.hbase.dao.BlockMapper;
import com.xyz.browser.app.modular.system.model.RtBlock;
import com.xyz.browser.app.modular.system.dao.RtBlockMapper;
import com.xyz.browser.app.modular.system.model.RtTxn;
import com.xyz.browser.app.modular.system.model.RtUncle;
import com.xyz.browser.app.modular.system.service.IRtBlockService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.xyz.browser.app.modular.system.service.IRtTxnService;
import com.xyz.browser.app.modular.system.service.IRtUncleService;
import com.xyz.browser.common.constant.Reward;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 实时block 服务实现类
 * </p>
 *
 * @author stylefeng
 * @since 2019-03-18
 */
@Service
@Slf4j
public class RtBlockServiceImpl extends ServiceImpl<RtBlockMapper, RtBlock> implements IRtBlockService {

    @Autowired
    private RtBlockMapper rtBlockMapper;
    @Autowired
    private IRtTxnService rtTxnService;
    @Autowired
    private IRtUncleService rtUncleService;
    @Autowired
    private DistributedRedisLock distributedRedisLock;

    private static CopyOptions co1;
    static{
        co1 = new CopyOptions();
        co1.setIgnoreNullValue(true);
    }
//    public static void main(String[] args) {
//        RtTxn a = new RtTxn();
//        a.setHash("5");
////        a.setFrom("2");
//        RtTxn b = new RtTxn();
//        b.setHash("1");
//        b.setFrom("3");
//        b.setStatus("4");
//        CopyOptions co = new CopyOptions().setIgnoreNullValue();
//        co.setIgnoreNullValue(true);
//        BeanUtil.copyProperties(a,b,co);
//        System.out.println(b);
//    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertRtData(RtBlock rtBlock, List<RtTxn> rtTxnList,List<String> uncles) {
        try{
            distributedRedisLock.acquire(RedisLockConstant.RT_BLOCK+"-"+rtBlock.getHash());
            if(CollUtil.isNotEmpty(uncles)){
                log.info(rtBlock+"with uncles");
                for(String uncleHash:uncles){
                    log.info("unclehash:"+uncleHash);
                    try {
                        distributedRedisLock.acquire(RedisLockConstant.RT_BLOCK+"-"+uncleHash);
                        RtUncle rtUncle = rtUncleService.selectById(uncleHash);
                        if (rtUncle == null)
                            rtUncle = new RtUncle();
                        rtUncle.setHash(uncleHash);
                        rtUncle.setBlockNumber(rtBlock.getNumber());
                        RtBlock uncleBlock = rtBlockMapper.selectById(uncleHash);
                        if (uncleBlock != null) {//
                            log.info("unclehash:"+uncleHash+" found in block,remove it");
                            BeanUtil.copyProperties(uncleBlock, rtUncle, co1);
                            rtUncle.setReward(Reward.UNCLES_REWARD);
                            rtBlockMapper.deleteById(uncleHash);
                        }
                        rtUncleService.insertOrUpdate(rtUncle);
                    }finally{
                        distributedRedisLock.release(RedisLockConstant.RT_BLOCK+"-"+uncleHash);
                    }
                }
//                this.insertOrUpdate(rtBlock);
            }
//            log.info(rtBlock.toString()+"with no uncles");
            RtUncle rtUncle = rtUncleService.selectById(rtBlock.getHash());
            if (rtUncle != null) {//is uncle
                log.info("block:"+rtBlock.getHash()+" is nucle");
                BeanUtil.copyProperties(rtBlock,rtUncle,co1);
                rtUncle.setReward(Reward.UNCLES_REWARD);
                rtUncleService.insertOrUpdate(rtUncle);
                rtBlockMapper.deleteById(rtBlock.getHash());
            }else{//maybe uncle or block
                log.info("block:"+rtBlock.getHash()+" maybe uncle or block");
                this.insertOrUpdate(rtBlock);
            }


        }finally{
            distributedRedisLock.release(RedisLockConstant.RT_BLOCK+"-"+rtBlock.getHash());
        }

        for(RtTxn rtTxn:rtTxnList){
            try {
                distributedRedisLock.acquire(RedisLockConstant.RT_TXN+"-"+rtTxn.getHash());
                RtTxn check = rtTxnService.selectById(rtTxn.getHash());
                if(check == null)
                    rtTxnService.insert(rtTxn);
                else{
                    BeanUtil.copyProperties(rtTxn,check,co1);
                    rtTxnService.updateById(check);
                }
            }finally{
                distributedRedisLock.release(RedisLockConstant.RT_TXN+"-"+rtTxn.getHash());
            }
        }

    }

    @Override
    public List<RtBlock> pageList(Map<String, Object> params) {
        return this.rtBlockMapper.pageList(params);
    }

    @Override
    public long pageCount(Map<String, Object> params) {
        return this.rtBlockMapper.pageCount(params);
    }
}
