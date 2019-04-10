package com.xyz.browser.app.modular.hbase.dao;

import cn.stylefeng.roses.core.mutidatasource.annotion.DataSource;
//import com.baomidou.mybatisplus.annotations.DataSource;
//import com.baomidou.mybatisplus.annotations.DataSource;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.xyz.browser.app.core.common.constant.DatasourceEnum;
import com.xyz.browser.app.modular.hbase.model.Block;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface BlockMapper {


    Block selectByHash(String hash);

    String selectHashByNumber(String number);

    String checkMiner(String address);
}