package ru.vsu.cs.Crocodile.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.vsu.cs.Crocodile.entities.Snippet;

import java.util.Calendar;
import java.util.Collection;

@Repository
public interface SnippetRepositories extends ReactiveMongoRepository<Snippet, String> {

    @Query(sort = "{datePublication : -1}")
    Flux<Snippet> findAllByDatePublicationIsBefore(Calendar calendar, Pageable pageable);

    @Query(sort = "{datePublication : -1}")
    Flux<Snippet> findAllBy(Pageable pageable);

    @Query(sort = "{datePublication : -1}")
    Flux<Snippet> findByAuthor(String user);

    @Query(sort = "{datePublication : -1}")
    Flux<Snippet> findByAuthorAndDatePublicationIsBefore(String user, Calendar calendar, Pageable pageable);

    Flux<Snippet> findSnippetsByListOfUsersLikedTheSnippetContains(String user);

    Flux<Snippet> findAllByAuthorInAndDatePublicationIsBefore(Collection<String> authors, Calendar calendar, Pageable pageable);
}