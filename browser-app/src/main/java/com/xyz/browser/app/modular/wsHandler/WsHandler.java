package com.xyz.browser.app.modular.wsHandler;

import com.xyz.browser.common.model.websocket.InMessage;
import org.springframework.web.socket.WebSocketSession;

public interface WsHandler {
    public void handle(WebSocketSession webSocketSession,InMessage im);
}
