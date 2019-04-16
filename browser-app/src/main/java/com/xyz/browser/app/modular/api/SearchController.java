package com.xyz.browser.app.modular.api;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xyz.browser.app.core.util.JsonResult;
import com.xyz.browser.app.modular.api.vo.ContractSearchVo;
import com.xyz.browser.app.modular.hbase.model.Block;
import com.xyz.browser.app.modular.hbase.model.Transaction;
import com.xyz.browser.app.modular.hbase.service.BlockService;
import com.xyz.browser.app.modular.hbase.service.TransactionService;
import com.xyz.browser.app.modular.system.model.Contract;
import com.xyz.browser.app.modular.system.model.RtTxn;
import com.xyz.browser.app.modular.system.service.IContractService;
import com.xyz.browser.app.modular.system.service.IRtTxnService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vns")
@Slf4j
public class SearchController {
    @Autowired
    private BlockService blockService;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private IRtTxnService rtTxnService;
    @Autowired
    private IContractService IContractService;


    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public JsonResult search(@RequestBody String searchName) {
        log.info("searchName:"+searchName);
        List<Map<String,String>> list = Lists.newArrayList();
        if(StringUtils.isNotBlank(searchName)){
            try {
                Block block = blockService.selectByHash(searchName);
                if (block != null) {
                    Map<String, String> map = Maps.newHashMap();
                    map.put("type", "block");
                    map.put("value", block.getHash());
                    list.add(map);
                } else {
                    String number = null;
                    try {
                        number = "0x" + Long.toHexString(Long.parseLong(searchName));
                    } catch (Exception e) {
                    }
                    if (StringUtils.isNotBlank(number)) {
                        block = blockService.selectByNumber(number);
                        if (block != null) {
                            Map<String, String> map = Maps.newHashMap();
                            map.put("type", "block");
                            map.put("value", block.getHash());
                            list.add(map);
                        }
                    }
                }
            }catch(Exception e){}


            try {
                Transaction transaction = transactionService.selectByHash(searchName);
                if (transaction != null) {
                    Map<String, String> map = Maps.newHashMap();
                    map.put("type", "txn");
                    map.put("value", transaction.getTransactionHash());
                    list.add(map);
                }
            }catch(Exception e){}

            //添加地址查询
            try{
                RtTxn rtTxn = rtTxnService.selectOne(new EntityWrapper<RtTxn>().where("`from`={0} or `to` = {1}",searchName,searchName).last("limit 1"));
                if(rtTxn!=null){
                    Map<String, String> map = Maps.newHashMap();
                    map.put("type", "address");
                    map.put("value", searchName);
                    list.add(map);
                }

            }catch(Exception e){}
//            transactionService.
//



            try {
                Map<String, Object> params = new HashMap<>();
                params.put("name",searchName);
                List<ContractSearchVo> contracts = IContractService.selectList(params);
                List<String> address = rtTxnService.selectListByAddress(searchName);
                if (contracts != null && contracts.size() > 0) {

                    for(String ad : address){
                        ContractSearchVo adOV = new ContractSearchVo();
                        adOV.setType("address");
                        adOV.setAddress(ad);
                        contracts.add(adOV);
                    }

                    Map<String, String> map = Maps.newHashMap();
                    map.put("type", "contract");
                    map.put("value", JSONObject.toJSONString(contracts));
                    list.add(map);

                } else {

                    Map<String, Object> params1 = new HashMap<>();
                    params1.put("symbol", searchName);
                    List<ContractSearchVo> contractList = IContractService.selectList(params1);
                    if (contractList != null && contractList.size() > 0) {

                        for(String ad : address){
                            ContractSearchVo adOV = new ContractSearchVo();
                            adOV.setType("address");
                            adOV.setAddress(ad);
                            contractList.add(adOV);
                        }

                        Map<String, String> map = Maps.newHashMap();
                        map.put("type", "contract");
                        map.put("value", JSONObject.toJSONString(contractList));

                        list.add(map);

                    } else {

                        if (address != null && address.size() > 0) {
                            for(String ad : address){
                                ContractSearchVo adOV = new ContractSearchVo();
                                adOV.setType("address");
                                adOV.setAddress(ad);
                                contractList.add(adOV);
                            }
                            Map<String, String> map = Maps.newHashMap();
                            map.put("type", "contract");
                            map.put("value", JSONObject.toJSONString(contractList));

                            list.add(map);
                        }

                    }

                }



            }catch(Exception e){}

        }




        return new JsonResult().addData("related",list);
    }
}
