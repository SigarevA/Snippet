package ru.vsu.cs.Crocodile.servicies;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.vsu.cs.Crocodile.entities.Comment;
import ru.vsu.cs.Crocodile.entities.Snippet;
import ru.vsu.cs.Crocodile.entities.TapeType;
import ru.vsu.cs.Crocodile.entities.User;
import ru.vsu.cs.Crocodile.repositories.SnippetRepositories;

import java.util.*;

@Service
public class SnippetService {

    private final Logger LOG = LoggerFactory.getLogger(SnippetService.class);

    private SnippetRepositories snippetRepositories;

    @Autowired
    public SnippetService(SnippetRepositories snippetRepositories) {
        this.snippetRepositories = snippetRepositories;
    }

    public Mono<Snippet> createSnippet(Snippet snippet) {
        snippet.setDatePublication(new GregorianCalendar());
        return snippetRepositories.save(snippet);
    }

    public Mono<Snippet> saveSnippet(Snippet snippet) {
        return snippetRepositories.save(snippet);
    }

    public Mono<Snippet> findSnippetByID(String id) {
        LOG.debug("find snippet by id");
        LOG.debug("SnippetID : {}", id);
        return snippetRepositories.findById(id);
    }

    public Mono<Snippet> addComment(Snippet snippet, Comment comment) {
        LOG.debug("method addComment");
        LOG.debug("snippet : {}", snippet);
        LOG.debug("comment : {}", comment);
        Collection<String> comments = snippet.getComments();
        if (Objects.isNull(comments)) {
            comments = new LinkedList<>();
            snippet.setComments(comments);
        }
        comments.add(comment.getId());
        return snippetRepositories.save(snippet);
    }

    public Flux<Snippet> findUserSnippets(User user) {
        LOG.debug("find snippets user, user : {}", user);
        return snippetRepositories.findByAuthor(user.getId());
    }

    public Flux<Snippet> getAllSnippets(Calendar calendar, int pageNum, int sizePage) {
        LOG.debug("get all snippets");
        return snippetRepositories.findAllByDatePublicationIsBefore(
            calendar,
            PageRequest.of(
                pageNum, sizePage
            )
        )
            .doOnNext(snip -> LOG.debug("snipContent : {}, snipDate : {}", snip.getContent(), snip.getDatePublication().getTimeInMillis()))
        ;
    }

    public Flux<Snippet> getLikedSnippetByUserV2(Mono<User> userMono) {
        return userMono
            .flatMapMany(user -> snippetRepositories.findSnippetsByListOfUsersLikedTheSnippetContains(user.getId()));
    }

    public Flux<Snippet> getLikedSnippetByUser(Mono<User> userMono) {
        return userMono
            .filter(user -> Objects.nonNull(user.getLikedSnippets()))
            .map(User::getLikedSnippets)
            .switchIfEmpty(Mono.just(Collections.emptyList()))
            .flatMapIterable(collect -> collect)
            .flatMap(this::findSnippetByID)
            ;
    }

    public Flux<Snippet> getSnippetsBySubscription(User user, Calendar calendar, int pageNum, int sizePage) {
        Collection<String> authors = user.getAuthors();
        authors = Objects.isNull(authors) ? Collections.emptyList() : authors;
        return snippetRepositories.findAllByAuthorInAndDatePublicationIsBefore(authors, calendar, PageRequest.of(pageNum, sizePage));
    }

    public Flux<Snippet> getTapeSnippets(User user, String tapeType, Calendar calendar, int pageNum) {
        LOG.debug("get type tape");
        LOG.debug("server get numPage : {}", pageNum);
        switch (TapeType.fromString(tapeType)) {
            case ALL:
                return getAllSnippets(calendar, pageNum, 15);
            case SUBSCRIPTION:
                return getSnippetsBySubscription(user, calendar, pageNum, 15);
            default:
                return Flux.empty();
        }
    }
}