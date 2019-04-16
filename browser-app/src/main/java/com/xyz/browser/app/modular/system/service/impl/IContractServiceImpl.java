package com.xyz.browser.app.modular.system.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.xyz.browser.app.modular.api.vo.ContractInfoVo;
import com.xyz.browser.app.modular.api.vo.ContractSearchVo;
import com.xyz.browser.app.modular.system.dao.IContractMapper;
import com.xyz.browser.app.modular.system.model.Contract;
import com.xyz.browser.app.modular.system.service.IContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class IContractServiceImpl extends ServiceImpl<IContractMapper, Contract> implements IContractService {

    @Autowired
    private IContractMapper iContractMapper;

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
}
