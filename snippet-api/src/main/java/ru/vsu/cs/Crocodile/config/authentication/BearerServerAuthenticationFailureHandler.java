package ru.vsu.cs.Crocodile.config.authentication;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class BearerServerAuthenticationFailureHandler implements ServerAuthenticationFailureHandler {

    @Value("${origin.url}")
    private String origin;

    @Override
    public Mono<Void> onAuthenticationFailure(WebFilterExchange webFilterExchange, AuthenticationException exception) {
        ServerHttpResponse serverHttpResponse = webFilterExchange.getExchange().getResponse();
        serverHttpResponse.setStatusCode(HttpStatus.UNAUTHORIZED);
        serverHttpResponse.getHeaders().setAccessControlAllowOrigin(origin);
        serverHttpResponse.getHeaders().setAccessControlMaxAge(8000L);
        serverHttpResponse.getHeaders().setAccessControlAllowHeaders(List.of(HttpHeaders.AUTHORIZATION));
        serverHttpResponse.getHeaders().setAccessControlExposeHeaders(List.of("Error"));
        serverHttpResponse.getHeaders().set("Error", "token don't valid");
        return Mono.empty();
    }
}
