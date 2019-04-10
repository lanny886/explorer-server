package com.xyz.browser.app.modular.wsHandler;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.google.common.collect.Lists;
import com.xyz.browser.app.core.util.SpringUtil;
import com.xyz.browser.app.core.websocket.MyWebSocketHandler;
import com.xyz.browser.app.modular.api.vo.WsRtBlockVo;
import com.xyz.browser.app.modular.api.vo.WsRtTxnVo;
import com.xyz.browser.app.modular.system.model.RtBlock;
import com.xyz.browser.app.modular.system.model.RtTxn;
import com.xyz.browser.app.modular.system.model.Summary;
import com.xyz.browser.app.modular.system.service.IRtBlockService;
import com.xyz.browser.app.modular.system.service.ISummaryService;
import com.xyz.browser.app.modular.system.service.impl.RtBlockServiceImpl;
import com.xyz.browser.app.modular.system.service.impl.SummaryServiceImpl;
import com.xyz.browser.common.enums.WsMsgTypeEnum;
import com.xyz.browser.common.model.RtBlockDto;
import com.xyz.browser.common.model.RtTxnDto;
import com.xyz.browser.common.model.websocket.InMessage;
import com.xyz.browser.common.model.websocket.OutMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.math.BigDecimal;
import java.util.List;

public class RtBlockHandler implements WsHandler {
    IRtBlockService rtBlockService = (IRtBlockService)SpringUtil.getBean(RtBlockServiceImpl.class);
    ISummaryService summaryService = (ISummaryService)SpringUtil.getBean(SummaryServiceImpl.class);
    @Override
    public void handle(WebSocketSession webSocketSession, InMessage im) {
        String to = im.getToUser();
        RtBlockDto rtBlockDto =((JSONObject) im.getContent().get("block")).toJavaObject(RtBlockDto.class);

//        RtBlock rtBlock = new RtBlock();
//        List<RtTxn> rtTxnList = Lists.newArrayList();
        List<WsRtTxnVo> wsRtTxnVoList = Lists.newArrayList();
//        BeanUtil.copyProperties(rtBlockDto,rtBlock);
//        rtBlock.setTxnCount(String.valueOf(rtBlockDto.getTxns().size()));
//        rtBlock.setUncleCount(String.valueOf(rtBlockDto.getUncles().size()));
        WsRtBlockVo wsRtBlockVo = new WsRtBlockVo();
        BeanUtil.copyProperties(rtBlockDto,wsRtBlockVo);
        for(RtTxnDto rtTxnDto:rtBlockDto.getTxns()){
//            RtTxn rtTxn = new RtTxn();
//            BeanUtil.copyProperties(rtTxnDto,rtTxn);
//            rtTxnList.add(rtTxn);

            WsRtTxnVo wsRtTxnVo = new WsRtTxnVo();
            BeanUtil.copyProperties(rtTxnDto,wsRtTxnVo);
            wsRtTxnVoList.add(wsRtTxnVo);
        }
        wsRtBlockVo.setTxns(wsRtTxnVoList);
        RtBlock preRtBlock = rtBlockService.selectOne(new EntityWrapper<RtBlock>().where("number = {0}",rtBlockDto.getNumber()-1).last("limit 1"));
        Summary summary = new Summary();
        if(preRtBlock!=null){
            long interval = Math.abs(Long.valueOf(rtBlockDto.getT())-Long.valueOf(preRtBlock.getT()));
            wsRtBlockVo.setInterval(String.valueOf(interval));
            if(interval > 0L)
                summary.setTps(new BigDecimal(String.valueOf(rtBlockDto.getTxns().size())).divide(new BigDecimal(String.valueOf(interval)),BigDecimal.ROUND_HALF_UP).toString());
        }
//        rtBlockService.insertRtData(rtBlock,rtTxnList,rtBlockDto.getUncles());

        summary.setLatestBlock(String.valueOf(rtBlockDto.getNumber()));
        summaryService.upt(summary);
        OutMessage outMessage = new OutMessage(WsMsgTypeEnum.LATEST_BLOCK.getName());
        outMessage.addData("block",wsRtBlockVo);
        MyWebSocketHandler.sendMessageToUser(to,new TextMessage(outMessage.toJson()));

    }
}