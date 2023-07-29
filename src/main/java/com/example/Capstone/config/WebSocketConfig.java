//package com.example.Capstone.config;
//
//import com.example.Capstone.SystemMessageHandler;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.socket.config.annotation.EnableWebSocket;
//
//@Configuration
//@EnableWebSocket
//public class WebSocketConfig implements WebSocketConfigurer {
//    private final SystemMessageHandler systemMessageHandler;
//
//    public WebSocketConfig(SystemMessageHandler systemMessageHandler) {
//        this.systemMessageHandler = systemMessageHandler;
//    }
//
//    @Override
//    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
//        registry.addHandler(systemMessageHandler, "/ws/system")
//                .setAllowedOrigins("*")
//                .withSockJS();
//    }
//}
