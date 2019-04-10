package com.xyz.browser.app.modular.api;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xyz.browser.app.core.util.JsonResult;
import com.xyz.browser.app.modular.hbase.model.Block;
import com.xyz.browser.app.modular.hbase.model.Transaction;
import com.xyz.browser.app.modular.hbase.service.BlockService;
import com.xyz.browser.app.modular.hbase.service.TransactionService;
import com.xyz.browser.app.modular.system.model.RtTxn;
import com.xyz.browser.app.modular.system.service.IRtTxnService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
        }




        return new JsonResult().addData("related",list);
    }
}
