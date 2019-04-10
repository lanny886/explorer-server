package com.xyz.browser.app.modular.hbase.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.xyz.browser.app.modular.hbase.model.Block;
import com.xyz.browser.app.modular.hbase.model.Transaction;

import java.io.Serializable;
import java.util.List;

public interface TransactionMapper {

    Transaction selectByHash(String hash);

    List<Transaction> selectByBlockNumber(String blockNumber);

    Long totalCount();
}