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
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.stylefeng.roses.core.base.controller.BaseController;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xyz.browser.app.config.properties.GunsProperties;
import com.xyz.browser.app.core.common.constant.state.ManagerStatus;
import com.xyz.browser.app.core.common.exception.BizExceptionEnum;
import com.xyz.browser.app.core.oss.OssOpt;
import com.xyz.browser.app.core.redission.DistributedRedisLock;
import com.xyz.browser.app.core.shiro.ShiroKit;
import com.xyz.browser.app.core.util.JsonResult;
import com.xyz.browser.app.core.util.JwtTokenUtil;
import com.xyz.browser.app.modular.api.dto.PasswordDto;
import com.xyz.browser.app.modular.api.vo.AddressLatestTxnVo;
import com.xyz.browser.app.modular.api.vo.AddressVo;
import com.xyz.browser.app.modular.hbase.model.AddressTransaction;
import com.xyz.browser.app.modular.hbase.model.Block;
import com.xyz.browser.app.modular.hbase.service.AddressTransactionService;
import com.xyz.browser.app.modular.hbase.service.BlockService;
import com.xyz.browser.app.modular.system.model.RtBlock;
import com.xyz.browser.app.modular.system.model.RtTxn;
import com.xyz.browser.app.modular.system.model.RtUncle;
import com.xyz.browser.app.modular.system.model.User;
import com.xyz.browser.app.modular.system.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/vns/account")
@Slf4j
public class AccountController extends BaseController {

    @Autowired
    private GunsProperties gunsProperties;

    @Autowired
    private IUserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private DistributedRedisLock distributedRedisLock;

    @Autowired
    private IRoleService roleService;


    @Autowired
    private OssOpt ossOpt;

    private static FastDateFormat sdf = FastDateFormat.getInstance("yyyy-MM-dd");
    @Autowired
    private IDeptService deptService;
    @Autowired
    private BlockService blockService;
    @Autowired
    private IRtBlockService rtBlockService;
    @Autowired
    private IRtTxnService rtTxnService;
    @Autowired
    private IRtUncleService rtUncleService;
    @Autowired
    private AddressTransactionService addressTransactionService;




    private static CopyOptions co1;
    static{
        co1 = new CopyOptions();
        //RtTxn to AddressLatestTxnVo
        Map<String,String> fm = Maps.newHashMap();
        fm.put("t","timestamp");
        co1.setFieldMapping(fm);
    }
//    private JdbcTemplate phoenixJdbcTemplate;
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public JsonResult info(HttpServletRequest request,String address) {
        if (StringUtils.isBlank(address)) {
            return new JsonResult();
        }
//        List<Block> blockList = blockService.list();
        AddressVo addressVo = new AddressVo();

        Map<String, Object> params = Maps.newHashMap();
        params.put("address", address);
        long txnCount = rtTxnService.pageCount(params);
        AddressTransaction addressTransaction = addressTransactionService.selectByAddress(address);
        addressVo.setVolume(addressTransaction.getVolume());
//        params.put("offset",0);
//        params.put("limit",25);
//        List<RtTxn> rtTxns = rtTxnService.pageList(params);
        List<AddressLatestTxnVo> addressLatestTxnVos = Lists.newArrayList();
//        if(rtTxns!=null){
//            for(RtTxn rtTxn:rtTxns){
//                AddressLatestTxnVo addressLatestTxnVo = new AddressLatestTxnVo();
//                BeanUtil.copyProperties(rtTxn,addressLatestTxnVo,co1);
//                if(address.equals(addressLatestTxnVo.getFrom())){
//                    addressLatestTxnVo.setIo("out");
//                }else if(address.equals(addressLatestTxnVo.getTo())){
//                    addressLatestTxnVo.setIo("in");
//                }
//                addressLatestTxnVos.add(addressLatestTxnVo);
//            }
//
//            addressVo.setLatestTxns(addressLatestTxnVos);
//        }
        addressVo.setTxnCount(String.valueOf(txnCount));
//        BigDecimal balance = BigDecimal.ZERO;
//        if (!blockService.checkMiner(address)){
//            balance = rtTxnService.balance(address);
//            addressVo.setBalance(balance.toString());
//        }else{
        addressVo.setBalance(rtTxnService.balanceFromOut(address));
//        }
        Integer minedBlocks = rtBlockService.selectCount(new EntityWrapper<RtBlock>().where("miner = {0}",address));


        addressVo.setMinedBlocks(String.valueOf(minedBlocks));

        Integer minedUncles = rtUncleService.selectCount(new EntityWrapper<RtUncle>().where("miner = {0}",address));

        addressVo.setMinedUncles(String.valueOf(minedUncles));
        return new JsonResult().addData("address",addressVo);
    }


}
