package com.xyz.browser.app.modular.system.service;

import com.baomidou.mybatisplus.service.IService;
import com.xyz.browser.app.modular.api.vo.ContractInfoVo;
import com.xyz.browser.app.modular.api.vo.ContractSearchVo;
import com.xyz.browser.app.modular.system.model.Contract;

import java.util.List;
import java.util.Map;


public interface IContractService extends IService<Contract> {

    void insertRtData(Contract Contract);

    List<Contract> pageList(Map<String,Object> params);

    long pageCount(Map<String, Object> params);

    List<Contract> transfersPageList(Map<String,Object> params);

    long transfersPageCount(Map<String,Object> params);

    ContractInfoVo info(String contract);

    Contract selectOverview(String hash);

    List<ContractSearchVo> selectList(Map<String,Object> params);

    String getTotal(String contract);

    int updateContract(Contract Contract);

    Contract selectContractByAddress(String contract);

    List<Contract> contractList();

}
