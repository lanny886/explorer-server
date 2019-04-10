package com.xyz.browser.app.modular.hbase.dao;

//import com.baomidou.mybatisplus.annotations.DataSource;
//import com.baomidou.mybatisplus.annotations.DataSource;

import com.xyz.browser.app.modular.hbase.model.BlockSync;
import com.xyz.browser.app.modular.hbase.model.TxnStatusSync;

import java.util.List;

public interface TxnStatusSyncMapper {


    void deleteByHash(String hash);

    List<TxnStatusSync> selectAll();
}