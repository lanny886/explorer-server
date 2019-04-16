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
import cn.hutool.core.collection.CollUtil;
import cn.stylefeng.roses.core.base.controller.BaseController;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xyz.browser.app.core.util.JsonResult;
import com.xyz.browser.app.core.websocket.MyWebSocketHandler;
import com.xyz.browser.app.modular.api.dto.PageDto;
import com.xyz.browser.app.modular.api.vo.*;
import com.xyz.browser.app.modular.hbase.model.Block;
import com.xyz.browser.app.modular.hbase.model.Btransaction;
import com.xyz.browser.app.modular.hbase.model.Transaction;
import com.xyz.browser.app.modular.hbase.service.BlockService;
import com.xyz.browser.app.modular.hbase.service.BlockSyncService;
import com.xyz.browser.app.modular.hbase.service.BtransactionService;
import com.xyz.browser.app.modular.hbase.service.TransactionService;
import com.xyz.browser.app.modular.system.model.RtBlock;
import com.xyz.browser.app.modular.system.model.RtTxn;
import com.xyz.browser.app.modular.system.model.Summary;
import com.xyz.browser.app.modular.system.service.*;
import com.xyz.browser.app.modular.system.service.impl.SummaryServiceImpl;
import com.xyz.browser.common.enums.WsMsgTypeEnum;
import com.xyz.browser.common.model.RtBlockDto;
import com.xyz.browser.common.model.RtTxnDto;
import com.xyz.browser.common.model.websocket.OutMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.TextMessage;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/vns/block")
@Slf4j
public class BlockController extends BaseController {

    private static FastDateFormat sdf = FastDateFormat.getInstance("yyyy-MM-dd");
    @Autowired
    private BlockService blockService;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private IRtBlockService rtBlockService;
    @Autowired
    private BtransactionService btransactionService;
    @Autowired
    private IRtTxnService rtTxnService;

    @Autowired
    private BlockSyncService blockSyncService;
    private static CopyOptions co1;
    static{
        co1 = new CopyOptions();
        //RtBlock to BlockPageVo
        Map<String,String> fm = Maps.newHashMap();
        fm.put("t","timestamp");
        fm.put("txnCount","txn");
        fm.put("uncleCount","uncles");
        co1.setFieldMapping(fm);
    }

    public static void main(String[] args) throws Exception {
        Summary summary = new Summary();
        System.out.println(SummaryServiceImpl.isAllFieldNull(summary));


    }
//    @RequestMapping(value = "/test", method = RequestMethod.POST)
//    public JsonResult test(@RequestBody String hash) {
//        blockSyncService.deleteByHash(hash);
//        return new JsonResult();
//    }

    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public JsonResult info(@RequestParam(required = false) String hash,
                          @RequestParam(required = false) String number) {
        BlockVo blockVo = null;
        Block block = null;
        if(StringUtils.isNotBlank(hash))
            block = blockService.selectByHash(hash);
        else if(StringUtils.isNotBlank(number)) {
            number = "0x"+Long.toHexString(Long.parseLong(number));
            block = blockService.selectByNumber(number);
        }
        if(block!=null){
            blockVo = new BlockVo();
            BeanUtil.copyProperties(block,blockVo);
            blockVo.setDifficulty(new BigInteger(blockVo.getDifficulty().substring(2),16).toString());
            blockVo.setGasLimit(new BigInteger(blockVo.getGasLimit().substring(2),16).toString());
            blockVo.setGasUsed(new BigInteger(blockVo.getGasUsed().substring(2),16).toString());
            BigInteger bigNum = new BigInteger(blockVo.getNumber().substring(2),16);
            blockVo.setNumber(bigNum.toString());
            blockVo.setSize(new BigInteger(blockVo.getSize().substring(2),16).toString());
            BigInteger bigT = new BigInteger(blockVo.getTimestamp().substring(2),16);
            blockVo.setTimestamp(bigT.toString());
            blockVo.setTotalDifficulty(new BigInteger(blockVo.getTotalDifficulty().substring(2),16).toString());

//            List<Btransaction> txns = btransactionService.selectByHash(block.getHash());
            JSONArray txns = JSON.parseArray(block.getTransactions());

            Integer txnCount=0;
            Integer ctxnCount=0;

            if(txns!=null){
                for(int i=0;i<txns.size();i++){
                    JSONObject obj = txns.getJSONObject(i);
                    String input = obj.getString("input");
                    if(StringUtils.isNotBlank(input) && !input.equals("0x")){
                        ctxnCount++;
                    }
                    txnCount++;
                }
            }
            blockVo.setTxnCount(String.valueOf(txnCount));
            blockVo.setCtxnCount(String.valueOf(ctxnCount));

            RtBlock preRtBlock = rtBlockService.selectOne(new EntityWrapper<RtBlock>().where("number = {0}",bigNum.longValue()-1).last("limit 1"));
            if(preRtBlock!=null){
                long interval = Math.abs(bigT.longValue()-Long.valueOf(preRtBlock.getT()));
                blockVo.setInterval(String.valueOf(interval));
            }
        }
        return new JsonResult().addData("block",blockVo);
    }

    @RequestMapping(value = "/rt", method = RequestMethod.GET)
    public JsonResult rt() {
        List<RtBlock> list = rtBlockService.selectList(new EntityWrapper<RtBlock>().orderBy("number",false).last("limit 10"));
        List<RtBlockVo> rtBlockVos = Lists.newArrayList();
        if(CollUtil.isNotEmpty(list)){
            RtBlock preBlock = rtBlockService.selectOne(new EntityWrapper<RtBlock>().where("number <{0}",list.get(list.size()-1).getNumber()).orderBy("number",false).last("limit 1"));
            for(int i=0;i<list.size();i++){
                RtBlockVo rtBlockVo = new RtBlockVo();
                BeanUtil.copyProperties(list.get(i),rtBlockVo);
                if(i!=list.size()-1){
                    rtBlockVo.setInterval(String.valueOf(Long.valueOf(rtBlockVo.getT())-Long.valueOf(list.get(i+1).getT())));
                }else{
                    if(preBlock!=null){
                        rtBlockVo.setInterval(String.valueOf(Long.valueOf(rtBlockVo.getT())-Long.valueOf(preBlock.getT())));
                    }else
                        rtBlockVo.setInterval("0");
                }
                rtBlockVos.add(rtBlockVo);

            }
        }

        return new JsonResult().addData("blocks",rtBlockVos);
    }

    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public JsonResult list(@RequestBody PageDto pageDto) {
        BigInteger page = new BigInteger(pageDto.getPage()).subtract(BigInteger.ONE);
        BigInteger limit = new BigInteger(pageDto.getLimit());
        if(page.intValue()<0 || limit.intValue() > 200){
            return new JsonResult();
        }
        BigInteger offset = page.multiply(limit);

//        List<RtBlock> rtBlocks = rtBlockService.selectList(new EntityWrapper<RtBlock>().orderBy("number",false).last("limit "+offset.toString()+","+limit.toString()));
        List<BlockPageVo> list = Lists.newArrayList();

        Map<String,Object> params = Maps.newHashMap();
        long total = rtBlockService.pageCount(params);
        params.put("offset",offset.longValue());
        params.put("limit",limit.intValue());
        List<RtBlock> rtBlocks = rtBlockService.pageList(params);
        long size ;
        if(total % limit.intValue() == 0){
            size = total/limit.intValue();
        }else{
            size = total/limit.intValue()+1;
        }
        for(RtBlock rtBlock:rtBlocks){
            BlockPageVo blockPageVo = new BlockPageVo();
            BeanUtil.copyProperties(rtBlock,blockPageVo,co1);
            list.add(blockPageVo);
        }

        return new JsonResult().addData("blocks",list).addData("total",String.valueOf(total)).addData("size",String.valueOf(size));
    }

    @RequestMapping(value = "/commit", method = RequestMethod.POST)
    public JsonResult commit(@RequestBody List<RtBlockDto> rtBlockDtos) {
        List<String> failedhashs = Lists.newArrayList();
        if(rtBlockDtos!=null){
            for(RtBlockDto rtBlockDto: rtBlockDtos){
                String hash=rtBlockDto.getHash();
                try{
                    RtBlock rtBlock = new RtBlock();
                    List<RtTxn> rtTxnList = Lists.newArrayList();
                    BeanUtil.copyProperties(rtBlockDto,rtBlock);

                    rtBlock.setTxnCount(String.valueOf(rtBlockDto.getTxns().size()));
                    rtBlock.setUncleCount(String.valueOf(rtBlockDto.getUncles().size()));
                    for(RtTxnDto rtTxnDto:rtBlockDto.getTxns()){
                        RtTxn rtTxn = new RtTxn();
                        BeanUtil.copyProperties(rtTxnDto,rtTxn);
                        rtTxnList.add(rtTxn);
                    }
                    rtBlockService.insertRtData(rtBlock,rtTxnList,rtBlockDto.getUncles());
                }catch(Exception e){
                    log.error("block commit failed:"+hash);
                    e.printStackTrace();
                    failedhashs.add(hash);
                }
            }
        }

        return new JsonResult().addData("failed",failedhashs);
    }


//    @RequestMapping(value = "/list2", method = RequestMethod.POST)
//    public JsonResult list2(@RequestBody PageDto pageDto) {
//        BigInteger page = new BigInteger(pageDto.getPage());//.subtract(BigInteger.ONE);
//        BigInteger limit = new BigInteger(pageDto.getLimit());
//        Page<RtBlock> pages = new Page<RtBlock>(page.intValue(),limit.intValue());
//        rtBlockService.selectPage(pages,new EntityWrapper<RtBlock>().orderBy("number",false));
//        long size ;
//        if(pages.getTotal() % limit.intValue() == 0){
//            size = pages.getTotal()/limit.intValue();
//        }else{
//            size = pages.getTotal()/limit.intValue()+1;
//        }
//        return new JsonResult().addData("blocks",pages.getRecords()).addData("total",String.valueOf(pages.getTotal())).addData("size",String.valueOf(size));
//    }





}
