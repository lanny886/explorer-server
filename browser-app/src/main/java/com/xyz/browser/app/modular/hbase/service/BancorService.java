package com.xyz.browser.app.modular.hbase.service;

import com.xyz.browser.app.modular.hbase.model.Bancor;

import java.util.List;

public interface BancorService {

    Bancor selectByContract(String Contract);

    List<Bancor> selectBanCor(String name);

}
