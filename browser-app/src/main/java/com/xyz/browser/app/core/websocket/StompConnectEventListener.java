package com.xyz.browser.app.core.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.messaging.SessionConnectEvent;

@Slf4j
public class StompConnectEventListener implements ApplicationListener<SessionConnectEvent> {

    @Autowired
    SocketSessionRegistry socketSessionRegistry;

    public void onApplicationEvent(SessionConnectEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        String user = sha.getSessionAttributes().get("user").toString();
        String sessionId = sha.getSessionId();
        log.info("Connect event [user:"+user+"; sessionId:"+sessionId+"]");
        socketSessionRegistry.registerSessionId(user,sessionId);
//        get header data from client
//        stompClient.connect({company: "1"}, function(frame) {
//        });
//        String  company = sha.getNativeHeader("company").get(0);
//        log.debug("Connect event [sessionId: " + sha.getSessionId() +"; company: "+ company + " ]");
    }
}