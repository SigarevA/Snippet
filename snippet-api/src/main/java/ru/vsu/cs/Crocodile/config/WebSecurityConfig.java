package ru.vsu.cs.Crocodile.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.server.WebFilter;
import ru.vsu.cs.Crocodile.config.authentication.BearerServerAuthenticationConverter;
import ru.vsu.cs.Crocodile.config.authentication.BearerServerAuthenticationFailureHandler;
import ru.vsu.cs.Crocodile.config.authentication.BearerTokenReactiveAuthenticationManager;

@Configuration
@EnableWebFluxSecurity
public class WebSecurityConfig {

    private final Logger LOG = LoggerFactory.getLogger(WebSecurityConfig.class);

    private BearerTokenReactiveAuthenticationManager bearerTokenReactiveAuthenticationManager;
    private BearerServerAuthenticationConverter bearerServerAuthenticationConverter;
    private WebFilter corsFilter;
    private BearerServerAuthenticationFailureHandler bearerServerAuthenticationFailureHandler;

    private static final String[] AUTH_WHITELIST = {
        "/v2/api-docs",
        "/swagger-resources",
        "/swagger-resources/**",
        "/configuration/ui",
        "/configuration/security",
        "/swagger-ui.html",
        "/webjars/**"
    };

    @Autowired
    public WebSecurityConfig(
            BearerTokenReactiveAuthenticationManager bearerTokenReactiveAuthenticationManager,
            BearerServerAuthenticationConverter bearerServerAuthenticationConverter,
            @Qualifier("corsFilter") WebFilter corsFilter,
            BearerServerAuthenticationFailureHandler bearerServerAuthenticationFailureHandler) {
        this.bearerTokenReactiveAuthenticationManager = bearerTokenReactiveAuthenticationManager;
        this.bearerServerAuthenticationConverter = bearerServerAuthenticationConverter;
        this.corsFilter = corsFilter;
        this.bearerServerAuthenticationFailureHandler = bearerServerAuthenticationFailureHandler;
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http ) {
        LOG.debug("http config");
        return http
                .httpBasic().disable()
                .logout().disable()
                .csrf().disable()
                .authorizeExchange()
                    .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .and()
                .authorizeExchange()
                    .pathMatchers("/api/**").authenticated()
                .and()
                .authorizeExchange().anyExchange().permitAll()
                .and()
                //.addFilterAt(corsFilter, SecurityWebFiltersOrder.CORS)
                .addFilterAt(bearerAuthenticationFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    private AuthenticationWebFilter bearerAuthenticationFilter() {
        LOG.debug("bearerAuthenticationFilter");
        AuthenticationWebFilter bearerAuthenticationFilter;
        ReactiveAuthenticationManager authManager;
        authManager = bearerTokenReactiveAuthenticationManager;
        bearerAuthenticationFilter = new AuthenticationWebFilter(authManager);
        bearerAuthenticationFilter.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers("/api/**"));
        bearerAuthenticationFilter.setServerAuthenticationConverter(bearerServerAuthenticationConverter);
        bearerAuthenticationFilter.setAuthenticationFailureHandler(bearerServerAuthenticationFailureHandler);
        return bearerAuthenticationFilter;
    }
}