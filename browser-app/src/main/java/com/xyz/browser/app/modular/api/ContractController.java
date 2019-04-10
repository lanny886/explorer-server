package com.xyz.browser.app.modular.api;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xyz.browser.app.core.util.JsonResult;
import com.xyz.browser.app.modular.api.dto.PageDto;
import com.xyz.browser.app.modular.api.dto.TransfersPageDto;
import com.xyz.browser.app.modular.api.vo.ContractInfoVo;
import com.xyz.browser.app.modular.api.vo.ContractPageVo;
import com.xyz.browser.app.modular.api.vo.ContractTransfersVo;
import com.xyz.browser.app.modular.hbase.service.ContractService;
import com.xyz.browser.app.modular.system.model.Contract;
import com.xyz.browser.app.modular.system.service.IContractService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vns/contract")
@Slf4j
public class ContractController {

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

    @Autowired
    private IContractService iContractService;


    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public JsonResult contractList(@RequestBody PageDto pageDto) {

        BigInteger page = new BigInteger(pageDto.getPage()).subtract(BigInteger.ONE);
        BigInteger limit = new BigInteger(pageDto.getLimit());
        BigInteger offset = page.multiply(limit);
        List<ContractPageVo> list = Lists.newArrayList();
        Map<String,Object> params = Maps.newHashMap();
        long total = iContractService.pageCount(params);
        params.put("offset",offset.longValue());
        params.put("limit",limit.intValue());

        List<Contract> contractList = iContractService.pageList(params);
        long size ;
        if(total % limit.intValue() == 0){
            size = total/limit.intValue();
        }else{
            size = total/limit.intValue()+1;
        }

        for(Contract rtBlock:contractList){
            ContractPageVo contract = new ContractPageVo();
            BeanUtil.copyProperties(rtBlock,contract,co1);
            list.add(contract);
        }
        return new JsonResult().addData("contracts",list).addData("total",String.valueOf(total)).addData("size",String.valueOf(size));
    }


    @RequestMapping(value = "/transfersList", method = RequestMethod.POST)
    public JsonResult transfersList(@RequestBody TransfersPageDto pageDto) {

        BigInteger page = new BigInteger(pageDto.getPage()).subtract(BigInteger.ONE);
        BigInteger limit = new BigInteger(pageDto.getLimit());
        BigInteger offset = page.multiply(limit);
        List<ContractTransfersVo> list = Lists.newArrayList();
        Map<String,Object> params = Maps.newHashMap();
        params.put("offset", offset.longValue());
        params.put("limit", limit.intValue());
        params.put("contract", pageDto.getContract());

        long total = iContractService.transfersPageCount(params);
        List<Contract> contractList = iContractService.transfersPageList(params);
        long size ;
        if(total % limit.intValue() == 0){
            size = total/limit.intValue();
        }else{
            size = total/limit.intValue()+1;
        }

        for(Contract rtBlock:contractList){
            ContractTransfersVo contract = new ContractTransfersVo();
            BeanUtil.copyProperties(rtBlock,contract,co1);
            contract.setTo(rtBlock.getTto());
            contract.setFrom(rtBlock.getTfrom());
            list.add(contract);
        }
        return new JsonResult().addData("transfers",list).addData("total",String.valueOf(total)).addData("size",String.valueOf(size));
    }


    @RequestMapping(value = "/info", method = RequestMethod.POST)
    public JsonResult info(@RequestBody JSONObject contract) {

        Map<String,String> params = Maps.newHashMap();
        params.put("contract", contract.getString("contract"));

        ContractInfoVo contractInfo = iContractService.info(contract.getString("contract"));

        return new JsonResult().addData("info",contractInfo);

    }



}
