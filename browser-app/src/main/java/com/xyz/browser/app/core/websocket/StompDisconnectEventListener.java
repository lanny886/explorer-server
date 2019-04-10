package com.xyz.browser.app.core.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Slf4j
public class StompDisconnectEventListener implements ApplicationListener<SessionDisconnectEvent> {

    @Autowired
    SocketSessionRegistry socketSessionRegistry;

    public void onApplicationEvent(SessionDisconnectEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        String user = sha.getSessionAttributes().get("user").toString();
        String sessionId = sha.getSessionId();
        log.info("disconnect event [user:"+user+"; sessionId:"+sessionId+"]");
        socketSessionRegistry.unregisterSessionId(user,sessionId);
    }
}