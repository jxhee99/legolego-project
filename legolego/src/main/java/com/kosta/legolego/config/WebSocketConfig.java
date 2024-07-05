package com.kosta.legolego.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // STOMP 프로토콜을 사용하여 메시지를 처리할 수 있도록 WebSocket 메시지 브로커를 활성화
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {  // 클라이언트가 websocket 서버에 연결하는데 사용할 STOMP 엔드포인트 등록
        // 클라이언트가 /ws 엔드포인트를 통해 websocket 연결
        registry.addEndpoint("/ws").setAllowedOrigins("*").withSockJS();// withSockJS() : 브라우저가 WebSocket을 지원하지 않을 때 HTTP를 통해 연결할 수 있도록 함
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) { // 메시지 브로커를 구성
        // 메모리 내(Simple) 메시지 브로커를 활성화
        registry.enableSimpleBroker("/topic"); // /topic 으로 시작하는 목적지 구독하는 클라이언트에게 메시지 브로드캐스팅
        // 애플리케이션에서 메시지를 수신할 때 사용할 목적지 접두사 (우리는 딱히 필요 없을 듯)
//        registry.setApplicationDestinationPrefixes("/app"); // /app 으로 시작하는 목적지로 메시지를 전송하면, 해당 메시지 @MessageMapping이 붙은 컨트롤러로 라우팅
    }
}
