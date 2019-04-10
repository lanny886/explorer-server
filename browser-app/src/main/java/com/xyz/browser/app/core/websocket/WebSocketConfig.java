package com.xyz.browser.app.core.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.session.Session;
import org.springframework.session.web.socket.config.annotation.AbstractSessionWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;


@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig  implements WebSocketMessageBrokerConfigurer {

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {

        return new ServerEndpointExporter();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic","/queue");
        config.setApplicationDestinationPrefixes("/app");
        // 点对点使用的订阅前缀（客户端订阅路径上会体现出来），不设置的话，默认也是/user/
        config.setUserDestinationPrefix("/user");
//        config.setApplicationDestinationPrefixes("/app")
//
//                .enableStompBrokerRelay("/topic","/queue","/exchange")
//
//                .setRelayHost("localhost")
//                .setRelayPort(61613)
//                .setClientLogin("pig")
//                .setClientPasscode("lengleng")
//                .setSystemLogin("pig")
//                .setSystemPasscode("lengleng")
//                .setSystemHeartbeatSendInterval(5000)
//                .setSystemHeartbeatReceiveInterval(4000);

    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/websocket")
                .setAllowedOrigins("*")

                .addInterceptors(SessionAuthHandshakeInterceptor())
                .setHandshakeHandler(new MyHandshakeHandler());
        registry.addEndpoint("/socketjs")
                .setAllowedOrigins("*")
                .addInterceptors(SessionAuthHandshakeInterceptor())
                .setHandshakeHandler(new MyHandshakeHandler())
                .withSockJS();
    }

//    @Override
//    protected void configureStompEndpoints(StompEndpointRegistry registry) {
//        registry.addEndpoint("/websocket")
//                .setAllowedOrigins("*")
//
//                .addInterceptors(SessionAuthHandshakeInterceptor())
//                .setHandshakeHandler(new MyHandshakeHandler());
//        registry.addEndpoint("/socketjs")
//                .setAllowedOrigins("*")
//                .addInterceptors(SessionAuthHandshakeInterceptor())
//                .setHandshakeHandler(new MyHandshakeHandler())
//                .withSockJS();
//    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(PresenceChannelInterceptor());
    }


//    @Override
//    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
//        registration.addDecoratorFactory(new WebSocketHandlerDecoratorFactory() {
//            @Override
//            public WebSocketHandler decorate(WebSocketHandler webSocketHandler) {
//                return new EmaWebSocketHandlerDecorator(webSocketHandler);
//            }
//        });
//    }
    @Bean
    public SocketSessionRegistry SocketSessionRegistry(){
        return new SocketSessionRegistry();
    }


    @Bean
    public PresenceChannelInterceptor PresenceChannelInterceptor(){
        return new PresenceChannelInterceptor();
    }

    @Bean
    public SessionAuthHandshakeInterceptor SessionAuthHandshakeInterceptor(){
        return new SessionAuthHandshakeInterceptor();
    }

//    @Bean
//    public StompConnectEventListener StompConnectEventListener(){
//        return new StompConnectEventListener();
//    }
//    @Bean
//    public StompDisconnectEventListener StompDisconnectEventListener(){
//        return new StompDisconnectEventListener();
//    }

}