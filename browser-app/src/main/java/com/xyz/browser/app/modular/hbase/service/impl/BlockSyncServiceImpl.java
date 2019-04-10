/**
 * Copyright 2018-2020 stylefeng & fengshuonan (https://gitee.com/stylefeng)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xyz.browser.app.modular.hbase.service.impl;

import cn.stylefeng.roses.core.mutidatasource.annotion.DataSource;
import com.xyz.browser.app.core.common.constant.DatasourceEnum;
import com.xyz.browser.app.modular.hbase.dao.BlockMapper;
import com.xyz.browser.app.modular.hbase.dao.BlockSyncMapper;
import com.xyz.browser.app.modular.hbase.model.Block;
import com.xyz.browser.app.modular.hbase.model.BlockSync;
import com.xyz.browser.app.modular.hbase.service.BlockService;
import com.xyz.browser.app.modular.hbase.service.BlockSyncService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BlockSyncServiceImpl implements BlockSyncService {

    @Autowired
    private BlockSyncMapper blockSyncMapper;
//    @Override
//    @DataSource(name= DatasourceEnum.DATA_SOURCE_BIZ)
//    public List<Block> list() {
//        return blockMapper.list();
//    }

    @Override
    @DataSource(name= DatasourceEnum.DATA_SOURCE_BIZ)
    public void deleteByHash(String hash) {
        this.blockSyncMapper.deleteByHash(hash);
    }

    @Override
    @DataSource(name= DatasourceEnum.DATA_SOURCE_BIZ)
    public List<BlockSync> selectAll() {
        return this.blockSyncMapper.selectAll();
    }

}
