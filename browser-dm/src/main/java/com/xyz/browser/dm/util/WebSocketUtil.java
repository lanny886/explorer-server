package com.xyz.browser.dm.util;

import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class WebSocketUtil {
    public static ReconnectingWebSocketClient webSocketClient(String url) {
        try {
            ReconnectingWebSocketClient webSocketClient = new ReconnectingWebSocketClient(new URI(url),new Draft_6455()) {

                @Override
                public void open(ServerHandshake handshakedata) {
                    System.out.println("[websocket] 连接成功");
                }

                @Override
                public void message(String message) {
                    System.out.println("[websocket] 收到消息="+message);
                }

                @Override
                public void close(int code, String reason, boolean remote) {
                    System.out.println("[websocket] 退出连接");
                }

                @Override
                public void error(Exception ex) {
                    System.out.println("[websocket] 连接错误="+ex.getMessage());
                }

            };
            boolean isopen = webSocketClient.connectBlocking();
            if(isopen){
                System.out.println("打开了");
            }
            return webSocketClient;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
