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
import cn.hutool.core.collection.CollUtil;
import cn.stylefeng.roses.core.base.controller.BaseController;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.xyz.browser.app.core.util.JsonResult;
import com.xyz.browser.app.modular.api.dto.PageDto;
import com.xyz.browser.app.modular.api.vo.SummaryVo;
import com.xyz.browser.app.modular.hbase.model.Transaction;
import com.xyz.browser.app.modular.hbase.service.TransactionService;
import com.xyz.browser.app.modular.system.model.RankingAsset;
import com.xyz.browser.app.modular.system.model.RankingMiner;
import com.xyz.browser.app.modular.system.model.Summary;
import com.xyz.browser.app.modular.system.model.TxnDaily;
import com.xyz.browser.app.modular.system.service.IRankingAssetService;
import com.xyz.browser.app.modular.system.service.IRankingMinerService;
import com.xyz.browser.app.modular.system.service.ISummaryService;
import com.xyz.browser.app.modular.system.service.ITxnDailyService;
import com.xyz.browser.app.modular.system.service.impl.SummaryServiceImpl;
import lombok.extern.slf4j.Slf4j;
import net.sf.ehcache.search.aggregator.Sum;
import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.util.List;

import static sun.misc.Version.print;
import static sun.misc.Version.println;


@RestController
@RequestMapping("/api/vns/stat")
@Slf4j
public class StatController extends BaseController {

    private static FastDateFormat sdf = FastDateFormat.getInstance("yyyy-MM-dd");
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private ISummaryService summaryService;
    @Autowired
    private ITxnDailyService txnDailyService;
    @Autowired
    private IRankingAssetService rankingAssetService;
    @Autowired
    private IRankingMinerService rankingMinerService;
//
//    public static void main(String[] args) {
//        Summary s = new Summary();
//        s.setTtc("dfs");
//        JsonResult jr = new JsonResult().addData(s);
//        System.out.println(jr);
//    }

    @RequestMapping(value = "/summary", method = RequestMethod.GET)
    public JsonResult summary() {
        JsonResult result = new JsonResult();
        List<Summary> list = summaryService.selectList(new EntityWrapper<>());
        if(CollUtil.isNotEmpty(list)){
            SummaryVo summaryVo = new SummaryVo();
            BeanUtil.copyProperties(list.get(0),summaryVo);
            result.addData(summaryVo);
        }
        String circulation = rankingAssetService.selectCirculation();
        result.addData("circulation",circulation);
        return result;
    }

    @RequestMapping(value = "/txnDailyRecent", method = RequestMethod.GET)
    public JsonResult txnDailyRecent () {
        List<TxnDaily> list = txnDailyService.selectList(new EntityWrapper<TxnDaily>().orderBy("day",false).last("limit 14"));
        return new JsonResult().addData("txnDaily",list);
    }
    @RequestMapping(value = "/rankAsset", method = RequestMethod.POST)
    public JsonResult rankAsset (@RequestBody PageDto pageDto) {
        BigInteger page = new BigInteger(pageDto.getPage()).subtract(BigInteger.ONE);
        BigInteger limit = new BigInteger(pageDto.getLimit());
        if(page.intValue()<0 || limit.intValue() > 200){
            return new JsonResult();
        }
        BigInteger offset = page.multiply(limit);
        EntityWrapper ew = new EntityWrapper<RankingAsset>();
        int total= rankingAssetService.selectCount(ew);
        List<RankingAsset> list = rankingAssetService.selectList(ew.orderBy("rank").last("limit "+offset+","+limit));
        int size ;
        if(total % limit.intValue() == 0){
            size = total/limit.intValue();
        }else{
            size = total/limit.intValue()+1;
        }
        return new JsonResult().addData("ranks",list).addData("total",String.valueOf(total)).addData("size",String.valueOf(size));
    }

    @RequestMapping(value = "/rankMiner", method = RequestMethod.POST)
    public JsonResult rankMiner (@RequestBody PageDto pageDto) {
        BigInteger page = new BigInteger(pageDto.getPage()).subtract(BigInteger.ONE);
        BigInteger limit = new BigInteger(pageDto.getLimit());
        if(page.intValue()<0 || limit.intValue() > 200){
            return new JsonResult();
        }
        BigInteger offset = page.multiply(limit);
        EntityWrapper ew = new EntityWrapper<RankingAsset>();
        int total= rankingMinerService.selectCount(ew);
        List<RankingMiner> list = rankingMinerService.selectList(ew.orderBy("rank").last("limit "+offset+","+limit));
        int size ;
        if(total % limit.intValue() == 0){
            size = total/limit.intValue();
        }else{
            size = total/limit.intValue()+1;
        }
        return new JsonResult().addData("ranks",list).addData("total",String.valueOf(total)).addData("size",String.valueOf(size));

    }


}
