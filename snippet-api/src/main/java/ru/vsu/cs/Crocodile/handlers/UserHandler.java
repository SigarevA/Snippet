package ru.vsu.cs.Crocodile.handlers;

import com.google.cloud.storage.Storage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.codec.DecodingException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.vsu.cs.Crocodile.CustomException.ImageFieldIsNotInCorrectFormatException;
import ru.vsu.cs.Crocodile.CustomException.MultiPartDataEmptyException;
import ru.vsu.cs.Crocodile.CustomException.NoRequiredFieldException;
import ru.vsu.cs.Crocodile.DTO.MessageDTO;
import ru.vsu.cs.Crocodile.DTO.RepresentationAuthorDTO;
import ru.vsu.cs.Crocodile.DTO.StatusDTO;
import ru.vsu.cs.Crocodile.entities.User;
import ru.vsu.cs.Crocodile.servicies.*;

import java.util.Collection;
import java.util.Objects;

@Component
public class UserHandler {

    private static final Logger LOG = LoggerFactory.getLogger(UserHandler.class);
    private UserService userService;
    private SnippetService snippetService;
    private MapperService mapperService;
    private ImageService imageService;
    private GoogleStorageService googleStorageService;

    @Autowired
    public UserHandler(UserService userService,
                       SnippetService snippetService,
                       MapperService mapperService,
                       ImageService imageService,
                       GoogleStorageService googleStorageService
    ) {
        this.userService = userService;
        this.snippetService = snippetService;
        this.mapperService = mapperService;
        this.imageService = imageService;
        this.googleStorageService = googleStorageService;
    }

    public Mono<ServerResponse> handleGetInformationRequest(ServerRequest serverRequest) {
        return Mono.just(serverRequest.pathVariable("userId"))
            .flatMap(userService::findByID)
            .flatMap(user -> ServerResponse
                .ok().body(
                    Mono.just(user),
                    User.class
                )
            );
    }

    public Mono<ServerResponse> getUserAuthorsById(ServerRequest serverRequest) {
        String userId = serverRequest.pathVariable("userId");
        Mono<User> userMono = serverRequest.principal()
            .flatMap(principal -> userService.findUserByName(principal.getName()));
        Flux<RepresentationAuthorDTO> authorsFlux = userService.findByID(userId)
            .filter(user -> Objects.nonNull(user.getAuthors()))
            .flatMapIterable(User::getAuthors)
            .flatMap(authorId -> userService.findByID(authorId))
            .map(mapperService::converterUserToRepresentationAuthorDTO)
            .flatMap(author ->
                userMono.map(user -> {
                    Collection<String> authors = user.getAuthors();
                    LOG.debug("author : {}", author);
                    LOG.debug("user : {}", user);
                    if (Objects.nonNull(authors)) {
                        if (authors.contains(author.getId())) {
                            author.setSubscription(true);
                        }
                    }
                    return author;
                })
            );
        return ServerResponse.ok().body(
            authorsFlux,
            RepresentationAuthorDTO.class
        );
    }

    public Mono<ServerResponse> findFollowersUserByID(ServerRequest serverRequest) {
        String userId = serverRequest.pathVariable("userId");
        Mono<User> userMono = serverRequest.principal()
            .flatMap(principal -> userService.findUserByName(principal.getName()));
        Flux<RepresentationAuthorDTO> followersFlux = userService.findByID(userId)
            .filter(user -> Objects.nonNull(user.getFollowers()))
            .flatMapIterable(User::getFollowers)
            .flatMap(followerId -> userService.findByID(followerId))
            .map(mapperService::converterUserToRepresentationAuthorDTO)
            .flatMap(author ->
                userMono.map(user -> {
                        Collection<String> authors = user.getAuthors();
                        LOG.debug("author : {}", author);
                        LOG.debug("user : {}", user);
                        if (Objects.nonNull(authors)) {
                            if (authors.contains(author.getId())) {
                                author.setSubscription(true);
                            }
                        }
                        return author;
                    }
                )
            );
        return ServerResponse.ok().body(
            followersFlux,
            RepresentationAuthorDTO.class
        );
    }

    public Mono<ServerResponse> handleImageUserRequest(ServerRequest request) {
        LOG.debug("handler upload image");
        return request.principal()
            .flatMap(principal -> userService.findUserByName(principal.getName()))
            .flatMap(user -> imageService
                .getImageRepFromMultiForm(request.multipartData())
                .flatMap(imageService::handleFormFieldPart)
                .flatMap(source -> imageService.handleSource(source, user))
                .flatMap(tuple2 -> googleStorageService.createBlob(tuple2.getT1(), tuple2.getT2()))
                .flatMap( blob -> userService.addUserImage(user, blob.getBlobId().getName()) )
            )
            .flatMap(x -> ServerResponse.ok()
                .body(Mono.just(new StatusDTO("success")), StatusDTO.class)
            )
            .onErrorResume(DecodingException.class, ex -> ServerResponse.badRequest()
                .body(
                    Mono.just(
                        new MessageDTO("failure", "limit size")
                    ),
                    MessageDTO.class
                )
            )
            .onErrorResume(
                MultiPartDataEmptyException.class,  ex -> ServerResponse.badRequest()
                    .body(
                        Mono.just(
                            new MessageDTO("failure", "request does not contain multipartData")
                        ),
                        MessageDTO.class
                    )
            )
            .onErrorResume(
                NoRequiredFieldException.class, ex -> ServerResponse.badRequest()
                    .body(
                        Mono.just(
                            new MessageDTO("failure", "request does not contain image field")
                        ),
                        MessageDTO.class
                    )
            )
            .onErrorResume(
                ImageFieldIsNotInCorrectFormatException.class, ex -> ServerResponse.badRequest()
                    .body(
                        Mono.just(
                            new MessageDTO("failure", "Image field is not in correct format")
                        ),
                        MessageDTO.class
                    )
            )
        ;
    }
}