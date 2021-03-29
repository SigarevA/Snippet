package ru.vsu.cs.Crocodile.handlers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.vsu.cs.Crocodile.CustomException.TapeTypeNotDefined;
import ru.vsu.cs.Crocodile.DTO.*;
import ru.vsu.cs.Crocodile.entities.Snippet;
import ru.vsu.cs.Crocodile.entities.TapeType;
import ru.vsu.cs.Crocodile.entities.User;
import ru.vsu.cs.Crocodile.servicies.MapperService;
import ru.vsu.cs.Crocodile.servicies.SnippetService;
import ru.vsu.cs.Crocodile.servicies.UserService;

import java.security.Principal;
import java.util.*;

@Component
public class SnippetHandler {

    private SnippetService snippetService;
    private UserService userService;
    private MapperService mapperService;

    private final Logger LOG = LoggerFactory.getLogger(SnippetHandler.class);

    @Autowired
    public SnippetHandler(SnippetService snippetService, UserService userService, MapperService mapperService) {
        this.snippetService = snippetService;
        this.userService = userService;
        this.mapperService = mapperService;
    }

    public Mono<ServerResponse> getSnippetsOfTape(ServerRequest request) {
        String timestamp = request.pathVariable("timestamp");
        Optional<String> optionalPageNum = request.queryParam("pageNum");
        return request.principal()
            .flatMap(principal ->
                userService.findUserByName(principal.getName())
            )
            .flatMapMany(user ->
                Mono.just(user)
                    .filter(tUser -> Objects.nonNull(tUser.getTapeType()))
                    .map(User::getTapeType)
                    .switchIfEmpty(Mono.just(TapeType.ALL.getText()))
                    .zipWith(
                        Mono.just(timestamp)
                            .map(Long::parseLong) //to do change to cast
                            .map(time -> {
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTimeInMillis(time);
                                return calendar;
                            })
                    )
                    .flatMap(tuple2 -> Mono.just(optionalPageNum)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .map(Integer::parseInt)
                        .zipWith(Mono.just(tuple2))
                        .switchIfEmpty(Mono.zip(Mono.just(0), Mono.just(tuple2)))
                    )
                    .flatMapMany(tuple2 -> snippetService.getTapeSnippets(user, tuple2.getT2().getT1(), tuple2.getT2().getT2(), tuple2.getT1()))
                    .flatMap(mapperService::converterSnippetToSnippetDTO)
                    .sort((s1, s2) ->
                        -1 * s1.getDatePublication().compareTo(s2.getDatePublication()))
                    .map(snippet -> mapperService.checkUserMarkFavorite(user, snippet))

            )
            .collectList()
            .flatMap(snippets -> {
                    LOG.debug("snippets : {}", snippets);
                    return ServerResponse.ok().body(
                        Flux.fromIterable(snippets),
                        SnippetDTO.class
                    );
                }
            )
            .onErrorResume(NumberFormatException.class, ex -> ServerResponse.badRequest()
                .body(
                    Mono.just(
                        new MessageDTO("failure", "timestamp should be have long type")
                    ),
                    MessageDTO.class
                ))
            ;
    }

    public Mono<ServerResponse> createSnippet(ServerRequest request) {
        Mono<Snippet> snippetMono = request.bodyToMono(Snippet.class);
        Mono<? extends Principal> principalMono = request.principal();
        Mono<User> userMono = principalMono
            .flatMap(principal -> userService.findUserByName(principal.getName()));
        Mono<StatusDTO> res = Mono.zip(userMono, snippetMono)
            .map(tuple -> {
                Snippet snippet = tuple.getT2();
                LOG.debug("content : {}", snippet.getContent());
                snippet.setAuthor(tuple.getT1().getId());
                return snippet;
            })
            .flatMap(snippetService::createSnippet)
            .map(snippet -> new StatusDTO().setSuccess())
            .switchIfEmpty(Mono.just(new StatusDTO().setFailure()));
        return ServerResponse.ok()
            .body(res, StatusDTO.class);
    }

    public Mono<ServerResponse> likeSnippet(ServerRequest request) {
        LOG.debug("user like snippet");
        Mono<User> userMono = request.principal()
            .flatMap(principal -> userService.findUserByName(principal.getName()));
        Mono<SnippetDTO> snippetMono = request.bodyToMono(Snippet.class)
            .filter(snippet -> Objects.nonNull(snippet.getId()))
            .flatMap(snippet -> snippetService.findSnippetByID(snippet.getId()))
            .zipWith(userMono)
            .map(tuple -> {
                Snippet snippet = tuple.getT1();
                Collection<String> users = snippet.getListOfUsersLikedTheSnippet();
                User user = tuple.getT2();
                String userId = user.getId();
                if (Objects.isNull(users)) {
                    users = new LinkedList<>();
                    users.add(userId);
                    snippet.setListOfUsersLikedTheSnippet(users);
                } else {
                    int index = ((ArrayList<String>) users).indexOf(userId);
                    if (index == -1)
                        users.add(userId);
                    else
                        ((ArrayList<String>) users).remove(index);
                }
                return snippet;
            })
            .flatMap(snippetService::saveSnippet)
            .flatMap(mapperService::converterSnippetToSnippetDTO)
            .zipWith(userMono)
            .map(tuple -> {
                SnippetDTO snippet = tuple.getT1();
                User user = tuple.getT2();
                Collection<String> users = snippet.getListOfUsersLikedTheSnippet();
                if (Objects.isNull(users))
                    return snippet;
                if (users.contains(user.getId()))
                    snippet.setFavorite(true);
                return snippet;
            });
        return ServerResponse.ok()
            .body(
                snippetMono,
                SnippetDTO.class
            )
            ;
    }

    public Mono<ServerResponse> getUserSnippetsByID(ServerRequest request) {
        String userID = request.pathVariable("userID");
        LOG.debug("path variable : {}", userID);
        Mono<User> userMono = request.principal()
            .flatMap(principal -> userService.findUserByName(principal.getName()));
        Mono<User> authorMono = userService.findByID(userID);
        Flux<SnippetDTO> snippetDTOFlux = authorMono
            .flatMapMany(snippetService::findUserSnippets)
            .flatMap(mapperService::converterSnippetToSnippetDTO)
            .flatMap(snippetDTO -> userMono
                .map(user -> mapperService.checkUserMarkFavorite(user, snippetDTO))
            );
        return ServerResponse.ok().body(
            snippetDTOFlux,
            SnippetDTO.class
        );
    }

    @Operation(
        operationId = "getAuthorSnippets",
        parameters = {
            @Parameter(
                required = true,
                in = ParameterIn.HEADER,
                name = "Authorization"
            )
        }
    )
    public Mono<ServerResponse> getAuthorSnippets(ServerRequest request) {
        Flux<SnippetDTO> snippetFlux = request.principal()
            .flatMap(principal -> userService.findUserByName(principal.getName()))
            .flatMapMany(user ->
                userService.getAuthors(user)
                    .flatMap(snippetService::findUserSnippets)
                    .flatMap(mapperService::converterSnippetToSnippetDTO)
                    .map(snippet -> mapperService.checkUserMarkFavorite(user, snippet))
            );
        return ServerResponse.ok().body(
            snippetFlux,
            SnippetDTO.class
        );
    }

    public Mono<ServerResponse> getCategoryOfTapeContent(ServerRequest request) {
        Flux<TapeTypeRepresentation> respBody = request.principal()
            .flatMap(principal -> userService.findUserByName(principal.getName()))
            .flatMapMany(user -> Flux.fromArray(TapeType.values())
                .map(tapeType -> mapperService.convertTapeTypeToTapeTypeRepresentation(tapeType, user))
            );
        return ServerResponse.ok().body(
            respBody,
            TapeTypeRepresentation.class
        );
    }

    public Mono<ServerResponse> setupTapeType(ServerRequest request) {
        return request.principal()
            .flatMap(principal -> userService.findUserByName(principal.getName()))
            .flatMap(user -> request.bodyToMono(ChangingTapeTypeDTO.class)
                .filter(type -> TapeType.contain(type.name))
                .switchIfEmpty(Mono.error(new TapeTypeNotDefined()))
                .flatMap(type -> userService.setupTapeType(user, type.name))
            )
            .flatMap(user -> ServerResponse.ok().body(
                Flux.fromArray(TapeType.values())
                    .map(tapeType -> mapperService.convertTapeTypeToTapeTypeRepresentation(tapeType, user)),
                TapeTypeRepresentation.class
                )
            )
            .onErrorResume(throwable -> ServerResponse.badRequest()
                .body(
                    new MessageDTO("failure", "tape type not defined"),
                    MessageDTO.class
                )
            );
    }

    public Mono<ServerResponse> getTimestamp(ServerRequest request) {
        return ServerResponse.ok().body(
            Mono.just(Calendar.getInstance()),
            Calendar.class
        );
    }
}