package com.xyz.browser.app.modular.system.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.xyz.browser.app.modular.api.vo.ContractInfoVo;
import com.xyz.browser.app.modular.api.vo.ContractSearchVo;
import com.xyz.browser.app.modular.system.dao.IContractMapper;
import com.xyz.browser.app.modular.system.model.Contract;
import com.xyz.browser.app.modular.system.service.IContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class IContractServiceImpl extends ServiceImpl<IContractMapper, Contract> implements IContractService {

    @Autowired
    private IContractMapper iContractMapper;

    @Value("${guns.inf.total}")
    private String totalUrl;

    @Override
    public void insertRtData(Contract contract) {
        iContractMapper.insert(contract);
    }

    @Override
    public List<Contract> pageList(Map<String, Object> params) {
        return iContractMapper.pageList(params);
    }

    @Override
    public long pageCount(Map<String, Object> params) {
        return iContractMapper.pageCount(params);
    }

    @Override
    public List<Contract> transfersPageList(Map<String, Object> params) {
        return iContractMapper.transfersPageList(params);
    }

    @Override
    public long transfersPageCount(Map<String, Object> params) {
        return iContractMapper.transfersPageCount(params);
    }

    @Override
    public ContractInfoVo info(String contract) {
        return iContractMapper.info(contract);
    }

    @Override
    public Contract selectOverview(String hash) {
        return iContractMapper.selectOverview(hash);
    }

    @Override
    public List<ContractSearchVo> selectList(Map<String, Object> params) {
        return iContractMapper.selectList(params);
    }

    @Override
    public String getTotal(String contract) {
        HttpRequest post = HttpUtil.createPost(totalUrl);
        post.contentType("text/plain");
        post.body("{\"coin\":\"VNS\",\"contract\":\"" + contract + "\"}");
        HttpResponse response = post.execute();
        if (response.getStatus() == 200) {
            JSONObject obj = JSON.parseObject(response.body());
            String total = obj.getString("data");
            return total;
        }
        return null;
    }

    @Override
    public int updateContract(Contract contract) {
        return iContractMapper.updateContract(contract);
    }

    @Override
    public Contract selectContractByAddress(String contract) {
        return iContractMapper.selectContractByAddress(contract);
    }

    @Override
    public List<Contract> contractList() {
        return iContractMapper.contractLists();
    }
}
