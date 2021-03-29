package ru.vsu.cs.Crocodile.config;


import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.cors.reactive.CorsUtils;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import ru.vsu.cs.Crocodile.handlers.AuthHandler;
import ru.vsu.cs.Crocodile.handlers.CommentHandler;
import ru.vsu.cs.Crocodile.handlers.SnippetHandler;
import ru.vsu.cs.Crocodile.handlers.UserHandler;

import java.io.FileInputStream;
import java.io.IOException;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@EnableWebFlux
public class WebConfig implements WebFluxConfigurer {

    private final String PROJECT_ID = "snippet-vsu";
    private final static Logger LOG = LoggerFactory.getLogger(WebConfig.class);

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins("*") // any host or put domain(s) here
            .allowedMethods("GET, POST") // put the http verbs you want allow
            .allowedHeaders("Authorization, Content-Type, content-length");
    }

    @Bean
    @RouterOperations({
        @RouterOperation(
            path = "/auth",
            beanClass = AuthHandler.class,
            beanMethod = "authorizeUser"
        ),
        @RouterOperation(
            path = "/registration",
            beanClass = AuthHandler.class,
            beanMethod = "registrationUser"
        )
    }
    )
    public RouterFunction<ServerResponse> publicEndPoints(AuthHandler authHandler) {
        return route()
            .POST("/auth", authHandler::authorizeUser)
            .POST("/registration", authHandler::registrationUser)
            .build();
    }

    @Bean
    @RouterOperations({
        @RouterOperation(
            path = "/api/user/info",
            beanClass = UserHandler.class,
            beanMethod = "getUserInformationByID"
        ),
        @RouterOperation(
            path = "/api/authors",
            beanClass = UserHandler.class,
            beanMethod = "getUserAuthorsById"
        ),
        @RouterOperation(
            path = "/api/followers",
            beanClass = UserHandler.class,
            beanMethod = "findFollowersUserByID"
        )
    }
    )
    public RouterFunction<ServerResponse> apiUserRouters(UserHandler userHandler) {
        return route()
            .GET("/api/user/info/{userId}", userHandler::handleGetInformationRequest)
            .GET("/api/authors/{userId}", userHandler::getUserAuthorsById)
            .GET("/api/followers/{userId}", userHandler::findFollowersUserByID)
            .POST("/api/user/image", userHandler::handleImageUserRequest)
            .build()
            ;
    }

    @Bean
    @RouterOperations({
        @RouterOperation(
            path = "/api/snippet/all",
            method = {RequestMethod.GET},
            beanClass = SnippetHandler.class,
            beanMethod = "getAllSnippets"
        ),
        @RouterOperation(
            path = "/api/snippet/create",
            beanClass = SnippetHandler.class,
            method = {RequestMethod.POST},
            beanMethod = "createSnippet"
        ),
        @RouterOperation(
            path = "/api/snippets/subscription",
            beanClass = SnippetHandler.class,
            method = {RequestMethod.GET},
            beanMethod = "getAuthorSnippets"
        ),
        @RouterOperation(
            path = "/api/snippet/like",
            method = {RequestMethod.POST},
            beanClass = SnippetHandler.class,
            beanMethod = "likeSnippet"
        ),
        @RouterOperation(
            path = "/api/snippet/all/{userId}",
            beanClass = SnippetHandler.class,
            beanMethod = "getUserSnippetsByID"
        )
    })
    public RouterFunction<ServerResponse> apiSnippetRouters(SnippetHandler snippetHandler) {
        return route()
            .GET("/api/snippet/timestamp", snippetHandler::getTimestamp)
            .GET("/api/snippet/tape/{timestamp}", snippetHandler::getSnippetsOfTape)
            .POST("/api/tape/type", snippetHandler::setupTapeType)
            .POST("/api/snippet/create", snippetHandler::createSnippet)
            .GET("/api/snippet/subscription", snippetHandler::getAuthorSnippets)
            .POST("/api/snippet/like", snippetHandler::likeSnippet)
            .GET("/api/snippet/all/{userID}", snippetHandler::getUserSnippetsByID)
            .GET("/api/types", snippetHandler::getCategoryOfTapeContent)
            .build()
        ;
    }

    @Bean
    @RouterOperations({
        @RouterOperation(
            path = "/api/add/comment",
            beanClass = CommentHandler.class,
            beanMethod = "addComment"
        ),
        @RouterOperation(
            path = "/api/like/comment",
            beanClass = CommentHandler.class,
            method = {RequestMethod.GET},
            beanMethod = "likeComment",
            operation = @Operation(
                operationId = "/api/like/comment",
                summary = "Like user comment"
            )
        ),
        @RouterOperation(
            path = "/api/comment",
            beanClass = CommentHandler.class,
            beanMethod = "getCommentByID"
        ),
        @RouterOperation(
            path = "/api/comments",
            beanClass = CommentHandler.class,
            beanMethod = "getCommentsBySnippetId"
        )
    })
    public RouterFunction<ServerResponse> apiCommentRouters(CommentHandler commentHandler) {
        return route()
            .POST("/api/add/comment", commentHandler::addComment)
            .GET("/api/comment", commentHandler::getCommentByID)
            .POST("/api/like/comment", commentHandler::likeComment)
            .GET("/api/comments", commentHandler::getCommentsBySnippetId)
            .build()
            ;
    }

    @Bean
    public RouterFunction<ServerResponse> apiEndPoints(AuthHandler authHandler) {
        return route()
            .GET("/api/user/info", authHandler::getUserInformation)
            .GET("/api/user/snippets", authHandler::getUserSnippet)
            .POST("/api/subscribe", authHandler::subscribeToAuthor)
            .POST("/api/unsubscribe", authHandler::unsubscribeAuthor)
            .GET("/api/liked/snippet", authHandler::getLikedSnippets)
            .build();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/swagger-ui.html**")
            .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
            .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    @Bean
    public Resource loadJsonKey() {
        return new ClassPathResource("Snippet-vsu-527130fdbf01.json");
    }

    @Bean
    public Mono<Credentials> getCredentials(@Qualifier("loadJsonKey") Resource resource) {
        Credentials credentials = null;
        LOG.debug("getFileName : {}", resource.getFilename());
        try {
            credentials = GoogleCredentials
                .fromStream(resource.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        LOG.debug("credentials : {}", credentials);
        return Mono.justOrEmpty( credentials ).doOnNext(cred -> LOG.debug("credit next") );
    }

    @Bean
    public Mono<Storage> getStorage(Mono<Credentials> credentialsMono) {
        return credentialsMono.map(
               credentials -> StorageOptions.newBuilder()
                    .setCredentials(credentials)
                   .setProjectId(PROJECT_ID)
                   .build()
            )
            .map(StorageOptions::getService);
    }

   /* @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.addAllowedOrigin("http://localhost:4200");
        corsConfig.addAllowedHeader("*");
        corsConfig.addAllowedHeader("Сontent-type");
        corsConfig.setMaxAge(8000L);
        corsConfig.addAllowedMethod("PUT");
        corsConfig.addAllowedMethod("POST");
        corsConfig.addAllowedMethod("OPTIONS");
        corsConfig.addAllowedMethod("GET");
        corsConfig.addAllowedHeader("Сontent-type");
        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }*/

    private static final String ALLOWED_HEADERS = "x-requested-with, authorization, Content-Type, Authorization, credential, X-XSRF-TOKEN";
    private static final String ALLOWED_METHODS = "GET, PUT, POST, DELETE, OPTIONS";
    private static final String ALLOWED_ORIGIN = "*";
    private static final String MAX_AGE = "3600";

    @Bean
    public WebFilter corsFilter() {
        return (ServerWebExchange ctx, WebFilterChain chain) -> {
            ServerHttpRequest request = ctx.getRequest();
            if (CorsUtils.isCorsRequest(request)) {
                ServerHttpResponse response = ctx.getResponse();
                HttpHeaders headers = response.getHeaders();
                headers.add("Access-Control-Allow-Origin", "http://localhost:4200");
                headers.add("Access-Control-Allow-Methods", ALLOWED_METHODS);
                headers.add("Access-Control-Max-Age", MAX_AGE);
                headers.add("Access-Control-Allow-Headers", ALLOWED_HEADERS);
                if (request.getMethod() == HttpMethod.OPTIONS) {
                    response.setStatusCode(HttpStatus.OK);
                    return Mono.empty();
                }
            }
            return chain.filter(ctx);
        };
    }
}