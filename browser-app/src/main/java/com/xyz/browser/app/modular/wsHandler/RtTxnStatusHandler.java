package com.xyz.browser.app.modular.wsHandler;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.xyz.browser.app.core.redission.DistributedRedisLock;
import com.xyz.browser.app.core.redission.RedisLockConstant;
import com.xyz.browser.app.core.util.SpringUtil;
import com.xyz.browser.app.core.websocket.MyWebSocketHandler;
import com.xyz.browser.app.modular.api.vo.WsRtBlockVo;
import com.xyz.browser.app.modular.api.vo.WsRtTxnVo;
import com.xyz.browser.app.modular.system.model.RtBlock;
import com.xyz.browser.app.modular.system.model.RtTxn;
import com.xyz.browser.app.modular.system.service.IRtBlockService;
import com.xyz.browser.app.modular.system.service.IRtTxnService;
import com.xyz.browser.app.modular.system.service.impl.RtBlockServiceImpl;
import com.xyz.browser.app.modular.system.service.impl.RtTxnServiceImpl;
import com.xyz.browser.common.enums.WsMsgTypeEnum;
import com.xyz.browser.common.model.RtBlockDto;
import com.xyz.browser.common.model.RtTxnDto;
import com.xyz.browser.common.model.websocket.InMessage;
import com.xyz.browser.common.model.websocket.OutMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;

public class RtTxnStatusHandler implements WsHandler {
    private IRtTxnService rtTxnService = (IRtTxnService)SpringUtil.getBean(RtTxnServiceImpl.class);

    private DistributedRedisLock distributedRedisLock = (DistributedRedisLock)SpringUtil.getBean(DistributedRedisLock.class);;
    @Override
    public void handle(WebSocketSession webSocketSession, InMessage im) {
//        String to = im.getToUser();
        RtTxnDto rtTxnDto =((JSONObject) im.getContent().get("txnStatus")).toJavaObject(RtTxnDto.class);
        RtTxn rtTxn = new RtTxn();
        BeanUtil.copyProperties(rtTxnDto,rtTxn);
        rtTxnService.updateStatus(rtTxn);

    }
}