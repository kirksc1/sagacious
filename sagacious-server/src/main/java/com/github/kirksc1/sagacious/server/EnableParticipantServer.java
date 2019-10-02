package com.github.kirksc1.sagacious.server;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Convenient annotation for Sagacious Participant Servers, enabling Saga Participant functionality over REST
 * endpoints.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(ParticipantServerConfiguration.class)
public @interface EnableParticipantServer {
}
