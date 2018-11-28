package com.iovation.service.clearkey.replayer.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

/**
 * Created by alice.martin
 * Developer: alice.martin
 * Date: 4/24/17
 * Time: 2:17 PM
 * Description: ${CARET}
 */
@Slf4j
@Component
public class StompDisconnectEvent  {


    @EventListener
    public void onApplicationEvent(SessionDisconnectEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        log.error(event.toString());
        log.error(sha.getMessage());
    }
}