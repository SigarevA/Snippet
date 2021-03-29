package ru.vsu.cs.Crocodile.servicies;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.vsu.cs.Crocodile.DTO.RepresentationCommentDTO;
import ru.vsu.cs.Crocodile.entities.Comment;
import ru.vsu.cs.Crocodile.entities.Snippet;
import ru.vsu.cs.Crocodile.entities.User;
import ru.vsu.cs.Crocodile.repositories.CommentRepository;

import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.Objects;

@Service
public class CommentService {
    private final Logger LOG = LoggerFactory.getLogger(CommentService.class);

    private CommentRepository commentRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public Mono<Comment> findById(String id) {
        return commentRepository.findById(id);
    }

    public Mono<Comment> createComment(User author, Comment comment) {
        LOG.debug("save comment");
        if (Objects.isNull(comment))
            return Mono.error(NullPointerException::new);
        comment.setDatePublication(new GregorianCalendar());
        comment.setAuthor(author.getId());
        return commentRepository.save(comment);
    }

    public Mono<Comment> findCommentById( String id ) {
        LOG.debug("find comment by id : {}", id);
        return commentRepository.findById(id);
    }

    public Mono<Comment> saveComment(Comment comment) {
        LOG.debug("save comment : {}", comment);
        return commentRepository.save(comment);
    }

    public Mono<RepresentationCommentDTO> checkUserLikeComment(Mono<User> userMono, RepresentationCommentDTO repComment) {
        return userMono.map(
                user -> {
                    Collection<String> likedUser = repComment.getLikedUser();
                    if (Objects.isNull(likedUser))
                        return repComment;
                    if (likedUser.contains(user.getId()))
                        repComment.setFavorite(true);
                    return repComment;
                }
        );
    }

}