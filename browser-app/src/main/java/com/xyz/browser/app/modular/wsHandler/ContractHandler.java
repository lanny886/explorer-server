package com.xyz.browser.app.modular.wsHandler;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.xyz.browser.app.core.util.SpringUtil;
import com.xyz.browser.app.modular.system.model.Contract;
import com.xyz.browser.app.modular.system.service.IContractService;
import com.xyz.browser.app.modular.system.service.impl.IContractServiceImpl;
import com.xyz.browser.common.model.ContractDto;
import com.xyz.browser.common.model.websocket.InMessage;
import org.springframework.web.socket.WebSocketSession;

public class ContractHandler implements WsHandler {

    private IContractService IContractService = (IContractService) SpringUtil.getBean(IContractServiceImpl.class);


    @Override
    public void handle(WebSocketSession webSocketSession, InMessage im) {

        try {

            ContractDto contractDto =((JSONObject) im.getContent().get("contract")).toJavaObject(ContractDto.class);
            Contract contract = new Contract();
            BeanUtil.copyProperties(contractDto,contract);

            IContractService.insertRtData(contract);

        } catch (Exception e) {

            e.printStackTrace();

        }


    }



}
