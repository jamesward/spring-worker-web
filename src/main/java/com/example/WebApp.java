package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.socket.*;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@Configuration
@EnableAutoConfiguration
@EnableWebSocket
public class WebApp implements WebSocketConfigurer {

    private ConcurrentHashMap<String, WebSocketSession> webSocketSessions = new ConcurrentHashMap<>();

    @RequestMapping(value = "/stock-update", method = RequestMethod.PUT)
    @ResponseBody
    void stockUpdate(@RequestBody Double price) throws IOException {
        System.out.println("Received stock price: " + price);
        for (WebSocketSession webSocketSession : webSocketSessions.values()) {
            System.out.println("Sending stock price to WebSocket: " + webSocketSession.getId());
            webSocketSession.sendMessage(new TextMessage(price.toString()));
        }
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketHandler(), "/ws").withSockJS();
    }

    @Bean
    public WebSocketHandler webSocketHandler() {
        return new TextWebSocketHandler() {
            @Override
            public void afterConnectionEstablished(WebSocketSession session) throws Exception {
                webSocketSessions.put(session.getId(), session);
            }

            @Override
            public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
                webSocketSessions.remove(session.getId());
            }
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(WebApp.class, args);
    }

}
