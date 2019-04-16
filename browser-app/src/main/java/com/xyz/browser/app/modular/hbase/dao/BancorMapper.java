package com.xyz.browser.app.modular.hbase.dao;

import com.xyz.browser.app.modular.hbase.model.Bancor;

import java.util.List;

public interface BancorMapper {

    Bancor selectByContract(String Contract);

    List<Bancor> selectBanCor(String name);

}
