package com.xyz.browser.app.modular.api;

import com.alibaba.fastjson.JSONArray;
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

import java.util.ArrayList;
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
//        List<Map<String,String>> list = Lists.newArrayList();
        JSONArray listjson = new JSONArray();
        if(StringUtils.isNotBlank(searchName)){
            try {
                Block block = blockService.selectByHash(searchName);
                if (block != null) {
                    Map<String, String> map = Maps.newHashMap();
                    map.put("type", "block");
                    map.put("value", block.getHash());
                    listjson.add(map);
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
                            listjson.add(map);
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
                    listjson.add(map);
                }
            }catch(Exception e){}

            //添加地址查询
            try{

                Contract contract = IContractService.selectContractByAddress(searchName);
                if (contract != null && !"".equals(contract.getContract())) {

                    Map<String, String> map = Maps.newHashMap();
                    map.put("type", "token");
                    map.put("value", contract.getContract());
                    listjson.add(map);

                } else {

                    RtTxn rtTxn = rtTxnService.selectOne(new EntityWrapper<RtTxn>().where("`from`={0} or `to` = {1}",searchName,searchName).last("limit 1"));
                    if(rtTxn!=null){
                        Map<String, String> map = Maps.newHashMap();
                        map.put("type", "address");
                        map.put("value", searchName);
                        listjson.add(map);
                    }

                }



            }catch(Exception e){
                e.printStackTrace();
            }
//            transactionService.
//



            try {

                Map<String, Object> params = new HashMap<>();
                params.put("name",searchName);
//                List<ContractSearchVo> VO = new ArrayList<>();
                JSONArray VO = new JSONArray();
                List<ContractSearchVo> contracts = IContractService.selectList(params);
                List<String> address = rtTxnService.selectListByAddress(searchName);
                Transaction transaction = transactionService.selectByHash(searchName);
                Block block = blockService.selectByHash(searchName);


                if (block != null) {
//                    ContractSearchVo vo = new ContractSearchVo();
                    JSONObject vo = new JSONObject();
                    vo.put("type","block");
                    vo.put("value",block.getHash());
//                    vo.setType("block");
//                    vo.setValue(block.getHash());
                    VO.add(vo);
                } else {
                    String number = null;
                    try {
                        number = "0x" + Long.toHexString(Long.parseLong(searchName));
                    } catch (Exception e) {
                    }
                    if (StringUtils.isNotBlank(number)) {
                        block = blockService.selectByNumber(number);
                        if (block != null) {
//                            ContractSearchVo vo = new ContractSearchVo();
//                            vo.setType("block");
//                            vo.setValue(block.getHash());
                            JSONObject vo = new JSONObject();
                            vo.put("type","block");
                            vo.put("value",block.getHash());
                            VO.add(vo);
                        }
                    }
                }

                if (transaction != null) {
                    ContractSearchVo vo = new ContractSearchVo();
                    vo.setType("txn");
                    vo.setValue(transaction.getTransactionHash());
                    VO.add(vo);
                }

                Contract contractInfo = IContractService.selectContractByAddress(searchName);
                if (contractInfo != null && !"".equals(contractInfo.getContract())) {
                    JSONObject vo = new JSONObject();
                    vo.put("type","token");
                    vo.put("value",contractInfo.getContract());
                    VO.add(vo);
                }

                RtTxn rtTxn = rtTxnService.selectOne(new EntityWrapper<RtTxn>().where("`from`={0} or `to` = {1}",searchName,searchName).last("limit 1"));
                if(rtTxn!=null){
                    JSONObject vo = new JSONObject();
                    vo.put("type","address");
                    vo.put("value",searchName);
                    VO.add(vo);
                } else {
                    for(String ad : address){
                        JSONObject vo = new JSONObject();
                        vo.put("type","address");
                        vo.put("value",ad);
                        VO.add(vo);
                    }
                }

                if (contracts != null && contracts.size() > 0) {

                    for(ContractSearchVo contract : contracts){
                        JSONObject vo = new JSONObject();
                        vo.put("type","token");
                        vo.put("value",contract.getContract());
                        vo.put("name", contract.getName());
                        vo.put("symbol", contract.getSymbol());
                        VO.add(vo);
                    }

                }

                JSONObject map = new JSONObject();
                map.put("type","intelligentQuery");
                map.put("value",VO);
                listjson.add(map);

            }catch(Exception e){}

        }




        return new JsonResult().addData("related",listjson);
    }
}
