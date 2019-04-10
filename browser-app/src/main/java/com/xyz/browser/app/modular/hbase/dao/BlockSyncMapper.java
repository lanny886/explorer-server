package com.xyz.browser.app.modular.hbase.dao;

//import com.baomidou.mybatisplus.annotations.DataSource;
//import com.baomidou.mybatisplus.annotations.DataSource;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.xyz.browser.app.modular.hbase.model.Block;
import com.xyz.browser.app.modular.hbase.model.BlockSync;

import java.util.List;

public interface BlockSyncMapper{


    void deleteByHash(String hash);

    List<BlockSync> selectAll();
}