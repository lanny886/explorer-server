package com.xyz.browser.app.modular.hbase.service.impl;

import cn.stylefeng.roses.core.mutidatasource.annotion.DataSource;
import com.xyz.browser.app.core.common.constant.DatasourceEnum;
import com.xyz.browser.app.modular.hbase.dao.ContractMapper;
import com.xyz.browser.app.modular.hbase.model.Contract;
import com.xyz.browser.app.modular.hbase.service.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ContractServiceImpl implements ContractService {

    @Autowired
    private ContractMapper contractMapper;

    @Override
    public List<Contract> selectAllList() {
        return contractMapper.selectAllList();
    }

    @Override
    @DataSource(name= DatasourceEnum.DATA_SOURCE_BIZ)
    public int updateContractTotal(Map<String, String> map) {
        return contractMapper.updateContractTotal(map);
    }


}
