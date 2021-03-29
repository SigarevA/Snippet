package ru.vsu.cs.Crocodile.servicies;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.vsu.cs.Crocodile.DTO.*;
import ru.vsu.cs.Crocodile.entities.Comment;
import ru.vsu.cs.Crocodile.entities.Snippet;
import ru.vsu.cs.Crocodile.entities.TapeType;
import ru.vsu.cs.Crocodile.entities.User;

import java.util.Collection;
import java.util.Objects;

@Service
public class MapperService {

    private final Logger LOG = LoggerFactory.getLogger(MapperService.class);
    private UserService userService;

    @Autowired
    public MapperService(UserService userService) {
        this.userService = userService;
    }

    public Mono<SnippetDTO> converterSnippetToSnippetDTO(Snippet snip) {
        LOG.debug("converter snippet to snippetDTO");
        LOG.debug("### author : {}", snip.getAuthor());
        if (Objects.isNull(snip.getAuthor()))
            return Mono.empty();
        LOG.debug("let's gi: {}", snip.getAuthor());
        Mono<User> user = userService.findByID(snip.getAuthor());
        return Mono.just(snip)
            .zipWith(user)
            .map(
                tuple -> {
                    Snippet snippet = tuple.getT1();
                    User author = tuple.getT2();
                    AuthorDTO authorDTO = new AuthorDTO(author.getId(), author.getName());
                    return new SnippetDTO(
                        snippet.getId(),
                        snippet.getContent(),
                        snippet.getDatePublication(),
                        authorDTO,
                        false,
                        snippet.getComments(),
                        snippet.getListOfUsersLikedTheSnippet()
                    );
                }
            );
    }

    public SnippetDTO converterSnippetToSnippetDTO2(Snippet snip) {
        LOG.debug("converter snippet to snippetDTO");
        LOG.debug("### author : {}", snip.getAuthor());
        LOG.debug("let's gi: {}", snip.getAuthor());
        Mono<User> user = userService.findByID(snip.getAuthor());
        AuthorDTO authorDTO = new AuthorDTO(snip.getAuthor(), "Author");
        return new SnippetDTO(
            snip.getId(),
            snip.getContent(),
            snip.getDatePublication(),
            authorDTO,
            false,
            snip.getComments(),
            snip.getListOfUsersLikedTheSnippet()
        );
    }

    public RepresentationAuthorDTO converterUserToRepresentationAuthorDTO(User user) {
        return new RepresentationAuthorDTO(user.getId(), user.getName(), false);
    }

    public SnippetDTO checkUserMarkFavorite(User user, SnippetDTO snippetDTO) {
        Collection<String> users = snippetDTO.getListOfUsersLikedTheSnippet();
        if (Objects.isNull(users))
            return snippetDTO;
        if (Objects.nonNull(user) && users.contains(user.getId()))
            snippetDTO.setFavorite(true);
        return snippetDTO;
    }

    public Mono<RepresentationCommentDTO> converterCommentToRepresentationCommentDTO(Comment comment) {
        String authorID = comment.getAuthor();
        if (Objects.isNull(authorID))
            return Mono.empty();
        return userService.findByID(authorID)
            .map(
                author ->
                    new RepresentationCommentDTO(
                        comment.getId(),
                        new AuthorDTO(author.getId(), author.getName()),
                        false,
                        comment.getDatePublication(),
                        comment.getContent(),
                        comment.getLikedUser()
                    )
            );
    }

    public TapeTypeRepresentation convertTapeTypeToTapeTypeRepresentation(TapeType tapeType, User user) {
        TapeType userTapeType = Objects.isNull(user.getTapeType()) ?
            TapeType.ALL :
            TapeType.fromString(user.getTapeType());
        return new TapeTypeRepresentation(tapeType.getText(), userTapeType == tapeType);
    }
}