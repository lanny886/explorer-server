package com.xyz.browser.app.modular.system.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.xyz.browser.app.modular.system.dao.HoldersMapper;
import com.xyz.browser.app.modular.system.model.Contract;
import com.xyz.browser.app.modular.system.model.Holders;
import com.xyz.browser.app.modular.system.service.HoldersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class HoldersServiceImpl extends ServiceImpl<HoldersMapper, Holders> implements HoldersService {

    @Autowired
    private HoldersMapper holdersMapper;

    @Override
    public List<Holders> pageList(Map<String, Object> params) {
        return holdersMapper.pageList(params);
    }

    @Override
    public long pageCount(Map<String, Object> params) {
        return holdersMapper.pageCount(params);
    }
}
