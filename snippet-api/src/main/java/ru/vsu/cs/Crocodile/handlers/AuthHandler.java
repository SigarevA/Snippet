package ru.vsu.cs.Crocodile.handlers;

import com.google.cloud.storage.Storage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import ru.vsu.cs.Crocodile.CustomException.DataIsIncorrectException;
import ru.vsu.cs.Crocodile.CustomException.EmailBusyException;
import ru.vsu.cs.Crocodile.DTO.*;
import ru.vsu.cs.Crocodile.entities.Snippet;
import ru.vsu.cs.Crocodile.entities.User;
import ru.vsu.cs.Crocodile.servicies.AuthService;
import ru.vsu.cs.Crocodile.servicies.SnippetService;
import ru.vsu.cs.Crocodile.servicies.UserService;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Objects;
import java.util.function.Function;

@Component
public class AuthHandler {

    private final static Logger LOG = LoggerFactory.getLogger(AuthHandler.class);
    private final Function<RegistrationDTO, User> converter = authDTO -> {
        LOG.debug("work converter");
        User user = new User();
        user.setEmail(authDTO.getEmail());
        user.setPassword(authDTO.getPassword());
        user.setName(authDTO.getName());
        return user;
    };

    private AuthService authService;
    private UserService userService;
    private SnippetService snippetService;
    private Mono<Storage> storageMono;

    @Autowired
    public AuthHandler(
        AuthService authService,
        UserService userService,
        SnippetService snippetService,
        Mono<Storage> storageMono
    ) {
        this.authService = authService;
        this.userService = userService;
        this.snippetService = snippetService;
        this.storageMono = storageMono;
    }

    private Tuple2<User, User> apply(Tuple2<User, User> tuple) {
        User follower = tuple.getT2();
        User author = tuple.getT1();
        Collection<String> authors = follower.getAuthors();
        if (Objects.isNull(authors)) {
            authors = new LinkedList<>();
            follower.setAuthors(authors);
        }
        Collection<String> followers = author.getFollowers();
        if (Objects.isNull(followers)) {
            followers = new LinkedList<>();
            author.setFollowers(followers);
        }
        return tuple;
    }

    @Operation(
        operationId = "authorizeUser",
        requestBody = @RequestBody(
            required = true,
            content = @Content(schema = @Schema(implementation = AuthDTO.class))
        )
    )
    public Mono<ServerResponse> authorizeUser(ServerRequest serverRequest) {
        Mono<AuthDTO> authDTOMono = serverRequest.bodyToMono(AuthDTO.class);
        Mono<Tuple2<String, String>> tokenMono = authDTOMono
            .flatMap(authService::authorize);
        Mono<AuthTokenDTO> statusDTOMono = tokenMono
            .map(tuple -> new AuthTokenDTO(StatusDTO.SUCCESS, tuple.getT1(), tuple.getT2()))
            .switchIfEmpty(Mono.just(new AuthTokenDTO(StatusDTO.FAILURE, null, null)));
        return ServerResponse.ok()
            .body(statusDTOMono, AuthTokenDTO.class)
            ;
    }

    public Mono<ServerResponse> registrationUser(ServerRequest serverRequest) {
        LOG.debug("handler registration user");
        Mono<RegistrationDTO> authDTOMono = serverRequest.bodyToMono(RegistrationDTO.class)
            .doOnNext(x -> LOG.debug("x : {}", x));
        Mono<? extends StatusDTO> resp = authDTOMono
            .flatMap(authService::check)
            .map(converter)
            .flatMap(authService::registrationUser)
            .map(x -> new StatusDTO().setSuccess())
            .switchIfEmpty(Mono.just(new StatusDTO().setFailure()))
            .onErrorResume(
                DataIsIncorrectException.class, e ->
                    Mono.just(new MessageDTO("failure", "form " + e.getMessage() + " is incorrect"))
            )
            .onErrorResume(
                EmailBusyException.class,
                e -> Mono.just(new MessageDTO("failure", "mail is busy"))
            );
        return ServerResponse.ok()
            .body(
                resp,
                StatusDTO.class)
            ;
    }

    public Mono<ServerResponse> getUserSnippet(ServerRequest serverRequest) {
        LOG.debug("get user snippets");
        Mono<User> userMono = serverRequest.principal()
            .flatMap(principal -> userService.findUserByName(principal.getName()));
        Flux<Snippet> snippetFlux = userMono
            .flatMapMany(snippetService::findUserSnippets);
        return ServerResponse.ok()
            .body(
                snippetFlux,
                Snippet.class
            )
            ;
    }

    public Mono<ServerResponse> getUserInformation(ServerRequest serverRequest) {
        LOG.debug("get user information");
        Mono<User> userMono = serverRequest.principal()
            .flatMap(principal -> userService.findUserByName(principal.getName()));
        return ServerResponse.ok()
            .body(
                userMono,
                User.class
            );
    }

    public Mono<ServerResponse> unsubscribeAuthor(ServerRequest request) {
        LOG.debug("handler unsubscribe to author");
        Mono<User> userMono = request.principal()
            .flatMap(principal -> userService.findUserByName(principal.getName()));
        Mono<? extends StatusDTO> status = request.bodyToMono(User.class)
            .filter(user -> Objects.nonNull(user.getId()))
            .flatMap(user -> userService.findByID(user.getId()))
            .zipWith(userMono)
            .map(this::apply)
            .flatMap(tuple -> userService.unsubscribe(tuple.getT1(), tuple.getT2()))
            .map(tuple -> new StatusDTO().setSuccess())
            .switchIfEmpty(Mono.just(new StatusDTO().setFailure()));
        return ServerResponse.ok()
            .body(status, StatusDTO.class);
    }

    public Mono<ServerResponse> subscribeToAuthor(ServerRequest request) {
        LOG.debug("User subscribe to author");
        Mono<User> userMono = request.principal()
            .flatMap(principal -> userService.findUserByName(principal.getName()));
        Mono<? extends StatusDTO> authorMono = request.bodyToMono(User.class)
            .filter(user -> Objects.nonNull(user.getId()))
            .flatMap(user -> userService.findByID(user.getId()))
            .zipWith(userMono)
            .filter(tuple -> {
                User author = tuple.getT1();
                User user = tuple.getT2();
                return !author.equals(user);
            })
            .map(this::apply)
            .filter(tuple -> !tuple.getT1().getFollowers().contains(tuple.getT2().getId()))
            .switchIfEmpty(Mono.error(new EmailBusyException()))
            .flatMap(tuple -> userService.subscribe(tuple.getT1(), tuple.getT2()))
            .map(tuple -> new StatusDTO().setSuccess())
            .switchIfEmpty(Mono.just(new StatusDTO().setFailure()))
            .onErrorReturn(EmailBusyException.class, new MessageDTO("failure", "mail busy"));
        return ServerResponse.ok()
            .body(authorMono, StatusDTO.class);
    }

    public Mono<ServerResponse> getLikedSnippets(ServerRequest request) {
        LOG.debug("get liked snippets");
        Mono<User> userMono = request.principal()
            .flatMap(principal -> userService.findUserByName(principal.getName()));
        return ServerResponse.ok()
            .body(snippetService.getLikedSnippetByUserV2(userMono), Snippet.class);
    }
}