package com.xyz.browser.app.modular.hbase.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.xyz.browser.app.modular.hbase.model.Btransaction;
import com.xyz.browser.app.modular.hbase.model.Transaction;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface BtransactionMapper {

    Btransaction selectByHash(String hash);

    Long totalCount();

    Long dailyCount(Map<String, Object> params);
}