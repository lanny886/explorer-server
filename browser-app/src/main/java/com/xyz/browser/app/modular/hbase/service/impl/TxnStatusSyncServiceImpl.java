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
import com.xyz.browser.app.modular.hbase.dao.TxnStatusSyncMapper;
import com.xyz.browser.app.modular.hbase.model.TxnStatusSync;
import com.xyz.browser.app.modular.hbase.service.TxnStatusSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TxnStatusSyncServiceImpl implements TxnStatusSyncService {

    @Autowired
    private TxnStatusSyncMapper txnStatusSyncMapper;
//    @Override
//    @DataSource(name= DatasourceEnum.DATA_SOURCE_BIZ)
//    public List<Block> list() {
//        return blockMapper.list();
//    }

    @Override
    @DataSource(name= DatasourceEnum.DATA_SOURCE_BIZ)
    public void deleteByHash(String hash) {
        this.txnStatusSyncMapper.deleteByHash(hash);
    }

    @Override
    @DataSource(name= DatasourceEnum.DATA_SOURCE_BIZ)
    public List<TxnStatusSync> selectAll() {
        return this.txnStatusSyncMapper.selectAll();
    }

}
