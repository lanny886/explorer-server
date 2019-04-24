package com.xyz.browser.app.core.websocket;


//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSON;
import com.xyz.browser.app.modular.wsHandler.*;
import com.xyz.browser.common.enums.WsMsgTypeEnum;
import com.xyz.browser.common.model.websocket.InMessage;
import com.xyz.browser.common.util.Json;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;

import static org.apache.shiro.web.filter.mgt.DefaultFilter.user;


@Service
@Slf4j
public class MyWebSocketHandler extends Object implements WebSocketHandler {

    //在线用户列表
//    private static final Map<String, WebSocketSession> users;

    private static final ConcurrentMap<String, Set<WebSocketSession>> users ;
    private final Object lock = new Object();

    static {
        users = new ConcurrentHashMap();
    }
    public static boolean checkClient(String clientId){
        return users.containsKey(clientId);
    }
    //新增socket
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("成功建立连接");
        String clientId = getClientId(session);
//        Object var3 = this.lock;
        synchronized(this.lock) {
            Set set = (Set)this.users.get(clientId);
            if(set == null) {
                set = new CopyOnWriteArraySet();
                this.users.put(clientId, (Set<WebSocketSession>) set);
            }

            ((Set)set).add(session);

            log.info("user:"+clientId+" size:"+set.size());
//            session.sendMessage(new TextMessage("成功建立socket连接"));
        }
    }

    //接收socket信息
    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws Exception {
        String payload = ((TextMessage) webSocketMessage).getPayload();
        try{

            InMessage inMessage = JSON.parseObject(payload, InMessage.class);
            WsHandler wsHandler = null;
            if(WsMsgTypeEnum.LATEST_BLOCK.getName().equals(inMessage.getReqType()))
                wsHandler = new RtBlockHandler();
//            else if(WsMsgTypeEnum.LATEST_TXN_STATUS.getName().equals(inMessage.getReqType()))
//                wsHandler = new RtTxnStatusHandler();
//            else if(WsMsgTypeEnum.TOTAL_TXN_COUNTER.getName().equals(inMessage.getReqType()))
//                wsHandler = new TtcHandler();
            else if(WsMsgTypeEnum.LATEST_CONTRACT.getName().equals(inMessage.getReqType()))
                wsHandler = new ContractHandler();

            if(wsHandler!=null)
                wsHandler.handle(webSocketSession,inMessage);
//            sendMessageToUser(inMessage.getToUser(),new TextMessage("服务器收到了，hello!"));
        }catch(Exception e){
            log.error("handleMessage err:"+payload);
            e.printStackTrace();
        }

    }

    /**
     * 发送信息给指定用户
     * @param clientId
     * @param message
     * @return
     */
    public static boolean sendMessageToUser(String clientId, TextMessage message) {
        Set<WebSocketSession> sessions = users.get(clientId);
        if(sessions == null || sessions.isEmpty()) return false;
        for(WebSocketSession session:sessions){
            log.info("sendMessage:" + session);
            if (!session.isOpen()) return false;
            try {
                session.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    /**
     * 广播信息
     * @param message
     * @return
     */
    public static boolean sendMessageToAllUsers(TextMessage message) {
        boolean allSendSuccess = true;
        Set<String> clientIds = users.keySet();
//        WebSocketSession session = null;
        for (String clientId : clientIds) {
            try {
                Set<WebSocketSession> sessions = users.get(clientId);
                if(sessions!=null && !sessions.isEmpty()){
                    for(WebSocketSession session:sessions){
                        if (session.isOpen()) {
                            session.sendMessage(message);
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
                allSendSuccess = false;
            }
        }

        return  allSendSuccess;
    }


    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {

        if (session.isOpen()) {
            session.close();
        }
        log.info("连接出错");
        log.info("session:"+session);
        String clientId = getClientId(session);
        log.info("user:"+clientId);
//        Object var3 = this.lock;
        synchronized(this.lock) {
            Set set = (Set)this.users.get(clientId);
            if(set != null && set.remove(session) ) {
                log.info("user:"+clientId+" size:"+set.size());
                if(set.isEmpty())
                    this.users.remove(clientId);
            }

        }

//        users.remove(getClientId(session));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("连接已关闭：" + status);
//        users.remove(getClientId(session));
        String clientId = getClientId(session);
        log.info("user:"+clientId);
        synchronized(this.lock) {
            Set set = (Set)this.users.get(clientId);
            if(set != null && set.remove(session) ) {
                log.info("user:"+clientId+" size:"+set.size());
                if(set.isEmpty())
                    this.users.remove(clientId);
            }

        }

    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    /**
     * 获取用户标识
     * @param session
     * @return
     */
    private String getClientId(WebSocketSession session) {
        try {
            String clientId = (String)session.getAttributes().get("user");
            return clientId;
        } catch (Exception e) {
            return null;
        }
    }
}

