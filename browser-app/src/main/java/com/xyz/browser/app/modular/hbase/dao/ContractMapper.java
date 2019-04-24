package com.xyz.browser.app.modular.hbase.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.xyz.browser.app.modular.hbase.model.Contract;

import java.util.List;
import java.util.Map;


public interface ContractMapper extends BaseMapper<Contract> {

    List<Contract> selectAllList();

    int updateContractTotal(Map<String, String> map);

}
