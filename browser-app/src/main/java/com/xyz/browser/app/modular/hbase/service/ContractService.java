package com.xyz.browser.app.modular.hbase.service;

import com.xyz.browser.app.modular.hbase.model.Contract;

import java.util.List;
import java.util.Map;

public interface ContractService {

    List<Contract> selectAllList();

    int updateContractTotal(Map<String, String> map);

}
