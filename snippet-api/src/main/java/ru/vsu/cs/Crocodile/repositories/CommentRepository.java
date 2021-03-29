package ru.vsu.cs.Crocodile.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import ru.vsu.cs.Crocodile.entities.Comment;

@Repository
public interface CommentRepository extends ReactiveMongoRepository<Comment, String> {
}