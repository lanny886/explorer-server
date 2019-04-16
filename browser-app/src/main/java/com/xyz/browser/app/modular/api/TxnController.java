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
package com.xyz.browser.app.modular.api;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.stylefeng.roses.core.base.controller.BaseController;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xyz.browser.app.core.util.JsonResult;
import com.xyz.browser.app.modular.api.dto.PageDto;
import com.xyz.browser.app.modular.api.dto.TxnPageDto;
import com.xyz.browser.app.modular.api.vo.BlockPageVo;
import com.xyz.browser.app.modular.api.vo.LogVo;
import com.xyz.browser.app.modular.api.vo.TransactionVo;
import com.xyz.browser.app.modular.api.vo.TxnPageVo;
import com.xyz.browser.app.modular.hbase.model.Block;
import com.xyz.browser.app.modular.hbase.model.Btransaction;
import com.xyz.browser.app.modular.hbase.model.Transaction;
import com.xyz.browser.app.modular.hbase.service.BlockService;
import com.xyz.browser.app.modular.hbase.service.BtransactionService;
import com.xyz.browser.app.modular.hbase.service.TransactionService;
import com.xyz.browser.app.modular.system.model.RtBlock;
import com.xyz.browser.app.modular.system.model.RtTxn;
import com.xyz.browser.app.modular.system.service.IRtBlockService;
import com.xyz.browser.app.modular.system.service.IRtTxnService;
import com.xyz.browser.common.model.RtBlockDto;
import com.xyz.browser.common.model.RtTxnDto;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/vns/txn")
@Slf4j
public class TxnController extends BaseController {

    private static FastDateFormat sdf = FastDateFormat.getInstance("yyyy-MM-dd");
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private BtransactionService btransactionService;
    @Autowired
    private BlockService blockService;
    @Autowired
    private IRtBlockService rtBlockService;
    @Autowired
    private IRtTxnService rtTxnService;

    private static CopyOptions co1;
    private static CopyOptions co2;

    static{
        co1 = new CopyOptions();
        //RtTxn to TxnPageVo
        Map<String,String> fm = Maps.newHashMap();
        fm.put("t","timestamp");
        co1.setFieldMapping(fm);

        co2 = new CopyOptions();
        Map<String,String> fm2 = Maps.newHashMap();
        fm2.put("transactionHash","hash");
        fm2.put("transactionIndex","position");
        String[] ignorePro = new String[]{"logs"};
        co2.setFieldMapping(fm2).setIgnoreProperties(ignorePro);
    }

    public static void main(String[] args) {
        BigInteger a = new BigInteger("9");
        BigInteger b = new BigInteger("4");
        System.out.println(a.divide(b).toString());
//        Transaction txn = new Transaction();
//        txn.setLogs("223");
//        TransactionVo transactionVo = new TransactionVo();
//        BeanUtil.copyProperties(txn,transactionVo,new CopyOptions().setIgnoreProperties(new String[]{"logs"}));
//        System.out.println(transactionVo);
    }

    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public JsonResult info(String hash) {
        TransactionVo transactionVo = new TransactionVo();
        Transaction transaction = transactionService.selectByHash(hash);
        if(transaction!=null){
            BeanUtil.copyProperties(transaction,transactionVo,co2);
//            System.out.println("t p:"+transaction.getTransactionIndex());
//            System.out.println("tv p:"+transactionVo.getPosition());
            BigInteger blockNumber = new BigInteger(transactionVo.getBlockNumber().substring(2),16);
            transactionVo.setBlockNumber(blockNumber.toString());

            RtBlock rtBlock = rtBlockService.selectOne(new EntityWrapper<RtBlock>().orderBy("number",false).last("limit 1"));
            if(rtBlock!=null && rtBlock.getNumber() - blockNumber.longValue()>=0L)
                transactionVo.setConfirms(String.valueOf(rtBlock.getNumber() - blockNumber.longValue()));
            else
                transactionVo.setConfirms("0");
            transactionVo.setPosition(new BigInteger(transactionVo.getPosition().substring(2),16).toString());
            transactionVo.setStatus(new BigInteger(transactionVo.getStatus().substring(2),16).toString());
            transactionVo.setGasUsed(new BigInteger(transactionVo.getGasUsed().substring(2),16).toString());
            List<LogVo> logs = JSON.parseArray(transaction.getLogs(), LogVo.class);
            if(logs!=null){
                for(LogVo logVo: logs){
                    logVo.setLogIndex(new BigInteger(logVo.getLogIndex().substring(2),16).toString());
                }
            }
            Btransaction btransaction = btransactionService.selectByHash(hash);
            if(btransaction!=null){
                transactionVo.setTimestamp(new BigInteger(btransaction.getTimestamp().substring(2),16).toString());
                transactionVo.setValue(new java.math.BigDecimal(new java.math.BigInteger(btransaction.getValue().substring(2),16).toString())
                        .divide(new java.math.BigDecimal("1000000000000000000")).toString());

                BigInteger gas = new BigInteger(btransaction.getGas().substring(2),16);
                BigInteger gasPrice = new BigInteger(btransaction.getGasPrice().substring(2),16);
                transactionVo.setGasPrice(gasPrice.toString());
                transactionVo.setTxnFee(gas.multiply(gasPrice).toString());
                transactionVo.setNonce(new BigInteger(btransaction.getNonce().substring(2),16).toString());
                transactionVo.setInputData(btransaction.getInput());
            }

            Block block = blockService.selectByHash(transaction.getBlockHash());
            if(block!=null) {
                transactionVo.setGasLimit(new BigInteger(block.getGasLimit().substring(2), 16).toString());
            }

            transactionVo.setLogs(logs);

        }


        return new JsonResult().addData("txn",transactionVo);
    }

    @RequestMapping(value = "/rt", method = RequestMethod.GET)
    public JsonResult rt() {
        Page<RtTxn> s;
        List<RtTxn> list = rtTxnService.selectList(new EntityWrapper<RtTxn>().orderBy("t",false).last("limit 10"));
        return new JsonResult().addData("txns",list);
    }
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public JsonResult list(@RequestBody TxnPageDto txnPageDto) {
        BigInteger page = new BigInteger(txnPageDto.getPage()).subtract(BigInteger.ONE);
        BigInteger limit = new BigInteger(txnPageDto.getLimit());
        if(page.intValue()<0 || limit.intValue() > 200){
            return new JsonResult();
        }
        BigInteger offset = page.multiply(limit);
        Map<String,Object> params = Maps.newHashMap();
        EntityWrapper ew = new EntityWrapper<RtTxn>();
        if(StringUtils.isNotBlank(txnPageDto.getBh()))
            params.put("block_hash",txnPageDto.getBh());
//            ew.and("block_hash={0}",txnPageDto.getBh());
        if(StringUtils.isNotBlank(txnPageDto.getStatus()))
            params.put("status",txnPageDto.getStatus());
//            ew.and("status = {0}",txnPageDto.getStatus());
        if(StringUtils.isNotBlank(txnPageDto.getAddress())){
            if("in".equals(txnPageDto.getIo()))
                params.put("to",txnPageDto.getAddress());
//                ew.and("to={0}",txnPageDto.getAddress());
            else if("out".equals(txnPageDto.getIo()))
                params.put("from",txnPageDto.getAddress());
//                ew.and("from={0}",txnPageDto.getAddress());
            else
                params.put("address",txnPageDto.getAddress());
//                ew.and().andNew("from={0} or to = {1}",txnPageDto.getAddress(),txnPageDto.getAddress());
        }



        long total = rtTxnService.pageCount(params);
        params.put("offset",offset.longValue());
        params.put("limit",limit.intValue());
        List<RtTxn> rtTxns = rtTxnService.pageList(params);
        long size ;
        if(total % limit.intValue() == 0){
            size = total/limit.intValue();
        }else{
            size = total/limit.intValue()+1;
        }
//        ew.orderBy("t",false).last("limit "+offset.toString()+","+limit.toString());
        List<TxnPageVo> list = Lists.newArrayList();
//        List<RtTxn> rtTxns = rtTxnService.selectList(ew);
        for(RtTxn rtTxn :rtTxns){
            TxnPageVo txnPageVo = new TxnPageVo();
            BeanUtil.copyProperties(rtTxn,txnPageVo,co1);
            if(StringUtils.isNotBlank(txnPageDto.getAddress())){
                if(txnPageDto.getAddress().equals(txnPageVo.getFrom())){
                    txnPageVo.setIo("out");
                }else if(txnPageDto.getAddress().equals(txnPageVo.getTo())){
                    txnPageVo.setIo("in");
                }
            }
            list.add(txnPageVo);
        }
        return new JsonResult().addData("txns",list).addData("total",String.valueOf(total)).addData("size",String.valueOf(size));
    }

//
//    @RequestMapping(value = "/list", method = RequestMethod.POST)
//    public JsonResult list(@RequestBody TxnPageDto txnPageDto) {
//        BigInteger page = new BigInteger(txnPageDto.getPage());
//        BigInteger limit = new BigInteger(txnPageDto.getLimit());
//
//        EntityWrapper ew = new EntityWrapper<RtTxn>();
//        if(StringUtils.isNotBlank(txnPageDto.getBh()))
//            ew.and("block_hash={0}",txnPageDto.getBh());
//        if(StringUtils.isNotBlank(txnPageDto.getStatus()))
//            ew.and("status = {0}",txnPageDto.getStatus());
//        if(StringUtils.isNotBlank(txnPageDto.getAddress())){
//            if("in".equals(txnPageDto.getIo()))
//                ew.and("to={0}",txnPageDto.getAddress());
//            else if("out".equals(txnPageDto.getIo()))
//                ew.and("from={0}",txnPageDto.getAddress());
//            else
//                ew.and().andNew("from={0} or to = {1}",txnPageDto.getAddress(),txnPageDto.getAddress());
//        }
//        ew.orderBy("t",false);//.last("limit "+offset.toString()+","+limit.toString());
//
//        Page<RtTxn> pages = new Page<>(page.intValue(),limit.intValue());
//        rtTxnService.selectPage(pages,ew);
//        //待优化
////        BigInteger total = new BigInteger(String.valueOf(rtTxnService.selectCount(ew)));
//        long size ;
//        if(pages.getTotal() % limit.intValue() == 0){
//            size = pages.getTotal()/limit.intValue();
//        }else{
//            size = pages.getTotal()/limit.intValue()+1;
//        }
//        List<TxnPageVo> list = Lists.newArrayList();
////        List<RtTxn> rtTxns = rtTxnService.selectList(ew);
//        for(RtTxn rtTxn :pages.getRecords()){
//            TxnPageVo txnPageVo = new TxnPageVo();
//            BeanUtil.copyProperties(rtTxn,txnPageVo,co1);
//            list.add(txnPageVo);
//        }
//
//
//
//        return new JsonResult().addData("txns",list).addData("total",String.valueOf(pages.getTotal())).addData("size",String.valueOf(size));
//    }

    @RequestMapping(value = "/commit", method = RequestMethod.POST)
    public JsonResult commit(@RequestBody List<RtTxnDto> rtTxnDtos) {
        List<String> failedhashs = Lists.newArrayList();
        if(rtTxnDtos!=null){
            for(RtTxnDto rtTxnDto: rtTxnDtos){
                String hash=rtTxnDto.getHash();
                try{
                    RtTxn rtTxn = new RtTxn();
                    BeanUtil.copyProperties(rtTxnDto,rtTxn);
                    rtTxnService.updateStatus(rtTxn);
                }catch(Exception e){
                    log.error("txnStatus commit failed:"+hash);
                    e.printStackTrace();
                    failedhashs.add(hash);
                }
            }
        }
        return new JsonResult().addData("failed",failedhashs);
    }

}
