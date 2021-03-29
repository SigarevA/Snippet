package ru.vsu.cs.Crocodile.handlers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.vsu.cs.Crocodile.DTO.*;
import ru.vsu.cs.Crocodile.entities.Comment;
import ru.vsu.cs.Crocodile.entities.Snippet;
import ru.vsu.cs.Crocodile.entities.User;
import ru.vsu.cs.Crocodile.servicies.CommentService;
import ru.vsu.cs.Crocodile.servicies.MapperService;
import ru.vsu.cs.Crocodile.servicies.SnippetService;
import ru.vsu.cs.Crocodile.servicies.UserService;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

@Component
public class CommentHandler {

    private final static Logger LOG = LoggerFactory.getLogger(CommentHandler.class);
    private final Function<AddingCommentDTO, Comment> converterAddingCommentDTOtoComment = addingCommentDTO -> {
        LOG.debug("convert comment");
        Comment comment = new Comment();
        comment.setContent(addingCommentDTO.getContent());
        return comment;
    };

    private SnippetService snippetService;
    private CommentService commentService;
    private UserService userService;
    private MapperService mapperService;

    public CommentHandler(SnippetService snippetService,
                          CommentService commentService,
                          UserService userService,
                          MapperService mapperService) {
        this.snippetService = snippetService;
        this.commentService = commentService;
        this.userService = userService;
        this.mapperService = mapperService;
    }


    @Operation(
        operationId = "/api/add/comment",
        summary = "Added user comment",
        security = {@SecurityRequirement(name = "bearer")},
        requestBody = @RequestBody(
            required = true,
            content = @Content(schema = @Schema(implementation = AddingCommentDTO.class))),
        responses = {
            @ApiResponse(
                responseCode = "200",
                content = @Content(
                    schema = @Schema(implementation = Snippet.class),
                    mediaType = MediaType.APPLICATION_JSON_VALUE
                )
            )
        }
    )
    public Mono<ServerResponse> addComment(ServerRequest serverRequest) {
        LOG.debug("handle add comment function");
        Mono<User> userMono = serverRequest.principal()
                .flatMap(principal -> userService.findUserByName(principal.getName()));

        Mono<AddingCommentDTO> addingCommentDTOMono = serverRequest.bodyToMono(AddingCommentDTO.class).cache();
        Mono<Snippet> snippetMono = addingCommentDTOMono
                .flatMap(addingCommentDTO -> snippetService.findSnippetByID(addingCommentDTO.getSnippetId()));

        Mono<Comment> commentMono = addingCommentDTOMono
                .map(converterAddingCommentDTOtoComment)
                .log()
                .flatMap(comment ->
                        userMono.flatMap(user -> commentService.createComment(user, comment))
                ).log();
        Mono<StatusDTO> response = snippetMono.log()
                .flatMap(snippet ->
                        commentMono.flatMap(comment -> snippetService.addComment(snippet, comment)).log()
                )
                .map(snippet -> new StatusDTO().setSuccess())
                .switchIfEmpty(Mono.just(new StatusDTO().setFailure()));
        return ServerResponse.ok().body(
                response,
                Snippet.class
        );
    }

    @Operation(
        requestBody = @RequestBody(
            content = @Content(
                schema = @Schema(implementation = CommentIDDTO.class)
            )
        )
    )
    public Mono<ServerResponse> likeComment(ServerRequest serverRequest) {
        LOG.debug("like comment");
        Mono<User> userMono = serverRequest.principal()
                .flatMap(principal -> userService.findUserByName(principal.getName()));
        Mono<StatusDTO> statusDTOMono = serverRequest.bodyToMono(CommentIDDTO.class)
                .filter(commentIDDTO -> Objects.nonNull(commentIDDTO.getCommentId()))
                .flatMap(commentIDDTO -> commentService.findById(commentIDDTO.getCommentId()))
                .map(
                        comment -> {
                            if (Objects.isNull(comment.getLikedUser())) {
                                comment.setLikedUser(new LinkedList<>());
                            }
                            return comment;
                        }
                )
                .flatMap(
                        comment ->
                                userMono.map(user -> {
                                            Collection<String> likedUsers = comment.getLikedUser();
                                            if (likedUsers.contains(user.getId()))
                                                likedUsers.remove(user.getId());
                                            else
                                                likedUsers.add(user.getId());
                                            return comment;
                                        }
                                ).flatMap(commentService::saveComment)
                )
                .map(comment -> new StatusDTO().setSuccess())
                .switchIfEmpty(Mono.just(new StatusDTO().setFailure()));
        return ServerResponse.ok().body(
                statusDTOMono,
                StatusDTO.class
        );
    }

    @Operation(
        operationId = "getCommentsBySnippetId",
        summary = "get comment with snippetId",
        security = {@SecurityRequirement(name = "bearer")},
        responses = {
            @ApiResponse(
                responseCode = "200",
                content = @Content(
                    schema = @Schema(implementation = RepresentationCommentDTO.class),
                    mediaType = MediaType.APPLICATION_JSON_VALUE
                )
            )
        }
    )
    public Mono<ServerResponse> getCommentsBySnippetId(ServerRequest serverRequest) {
        LOG.debug("get comments by snippetID");
        Optional<String> optSnippetID = serverRequest.queryParam("snippetID");
        if (optSnippetID.isEmpty()) {
            ServerResponse.badRequest().body(
                    Mono.just(new MessageDTO("error", "Required string parameter : snippetID")),
                    MessageDTO.class
            );
        }
        Mono<User> userMono = serverRequest.principal()
                .flatMap(principal -> userService.findUserByName(principal.getName()));
        String snippetId = optSnippetID.get();
        Flux<RepresentationCommentDTO> commets = snippetService
                .findSnippetByID(snippetId)
                .filter(snippet -> Objects.nonNull(snippet.getComments()))
                .flatMapIterable(Snippet::getComments)
                .flatMap(commentService::findById)
                .flatMap(mapperService::converterCommentToRepresentationCommentDTO)
                .flatMap(repComment -> commentService.checkUserLikeComment(userMono, repComment));
        return ServerResponse.ok().body(
                commets,
                RepresentationCommentDTO.class
        );
    }

    @Operation(
        operationId = "getCommentByID",
        parameters = {
            @Parameter(
                name = "commentID",
                in = ParameterIn.QUERY
            )
        },
        responses = {
            @ApiResponse(
                responseCode = "200",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = RepresentationCommentDTO.class)
                )
            )
        }
    )
    public Mono<ServerResponse> getCommentByID(ServerRequest serverRequest) {
        LOG.debug("get comments by commentID");
        Optional<String> optCommentID = serverRequest.queryParam("commentID");
        if (optCommentID.isEmpty()) {
            ServerResponse.badRequest().body(
                    Mono.just(new MessageDTO("error", "Required string parameter : commentID")),
                    MessageDTO.class
            );
        }
        Mono<User> userMono = serverRequest.principal()
                .flatMap(principal -> userService.findUserByName(principal.getName()));
        String commentID = optCommentID.get();
        Mono<RepresentationCommentDTO> representationCommentDTOMono = commentService
                .findById(commentID)
                .flatMap(mapperService::converterCommentToRepresentationCommentDTO)
                .flatMap(repComment -> commentService.checkUserLikeComment(userMono, repComment));
        return ServerResponse.ok().body(
                representationCommentDTOMono,
                RepresentationCommentDTO.class
        );
    }
}