package com.xyz.browser.app.core.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.stereotype.Component;

/**
 * 更好的方式SessionConnectEvent,SessionDisconnectEvent
 */

@Component
public class PresenceChannelInterceptor extends ChannelInterceptorAdapter {

    //private static final Logger logger = LoggerFactory.getLogger(PresenceChannelInterceptor.class);

    /**session操作类*/
    @Autowired
    SocketSessionRegistry socketSessionRegistry;

    @Override
    public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(message);
        // ignore non-STOMP messages like heartbeat messages
        if(sha.getCommand() == null) {
            return;
        }
        String user = sha.getSessionAttributes().get("user").toString();
        String sessionId = sha.getSessionId();
        //判断客户端的连接状态
        switch(sha.getCommand()) {
            case CONNECT:
                socketSessionRegistry.registerSessionId(user,sessionId);
                break;
            case DISCONNECT:
                socketSessionRegistry.unregisterSessionId(user,sessionId);
                break;
            default:
                break;
        }
    }



}