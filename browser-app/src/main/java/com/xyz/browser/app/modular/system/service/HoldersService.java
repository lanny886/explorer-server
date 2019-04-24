package com.xyz.browser.app.modular.system.service;


import com.baomidou.mybatisplus.service.IService;
import com.xyz.browser.app.modular.system.model.Holders;

import java.util.List;
import java.util.Map;

public interface HoldersService extends IService<Holders> {

    List<Holders> pageList(Map<String,Object> params);

    long pageCount(Map<String, Object> params);

}
