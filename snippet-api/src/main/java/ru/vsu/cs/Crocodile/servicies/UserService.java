package ru.vsu.cs.Crocodile.servicies;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import ru.vsu.cs.Crocodile.entities.Snippet;
import ru.vsu.cs.Crocodile.entities.User;
import ru.vsu.cs.Crocodile.repositories.UserRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

@Service
public class UserService {

    private final static Logger LOG = LoggerFactory.getLogger(UserService.class);

    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Mono<User> findUserByName(String email) {
        LOG.debug("find user by email : {}", email);
        return userRepository.findByEmail(email);
    }

    public Mono<User> findByID(String id) {
        LOG.debug("find user by id : {}", id);
        return userRepository.findById(id);
    }

    public Mono<User> setupTapeType(User user, String typeTape) {
        user.setTapeType(typeTape);
        return userRepository.save(user);
    }

    public Mono<User> addUserImage(User user, String nameObject) {
        user.setImagePath("https://storage.googleapis.com/snippet-images/" + nameObject);
        return userRepository.save(user);
    }

    public Mono<Tuple2<User, User>> subscribe(User author, User follower) {
        Collection<String> authors = follower.getAuthors();
        authors.add(author.getId());
        Collection<String> followers = author.getFollowers();
        followers.add(follower.getId());
        LOG.debug(" user : {} subscribe to author : {}", follower, author);
        return userRepository.save(author).zipWith(userRepository.save(follower));
    }

    public Mono<Tuple2<User, User>> unsubscribe(User author, User follower) {
        LOG.debug(" user : {} unsubscribe to author : {}", follower, author);
        Collection<String> authors = follower.getAuthors();
        int indexAuthor = ((ArrayList) authors).indexOf(author.getId());
        if (indexAuthor != -1)
            ((ArrayList) authors).remove(indexAuthor);
        Collection<String> followers = author.getFollowers();
        int indexFollower = ((ArrayList) followers).indexOf(follower.getId());
        if (indexFollower != -1)
            ((ArrayList<String>) followers).remove(indexFollower);
        return userRepository.save(author).zipWith(userRepository.save(follower));
    }

    public Flux<User> getAuthors(User user) {
        Collection<String> authors = user.getAuthors();
        if (Objects.isNull(authors))
            return Flux.empty();
        return Flux.fromIterable(authors)
                .flatMap( author -> userRepository.findById(author));
    }
}