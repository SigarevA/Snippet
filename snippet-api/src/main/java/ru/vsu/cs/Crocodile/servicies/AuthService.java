package ru.vsu.cs.Crocodile.servicies;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import ru.vsu.cs.Crocodile.CustomException.DataIsIncorrectException;
import ru.vsu.cs.Crocodile.CustomException.EmailBusyException;
import ru.vsu.cs.Crocodile.DTO.AuthDTO;
import ru.vsu.cs.Crocodile.DTO.RegistrationDTO;
import ru.vsu.cs.Crocodile.entities.TapeType;
import ru.vsu.cs.Crocodile.entities.User;
import ru.vsu.cs.Crocodile.jwt.JWTTokenService;
import ru.vsu.cs.Crocodile.repositories.UserRepository;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Objects;

@Service
public class AuthService {

    private final Logger LOG = LoggerFactory.getLogger(AuthService.class);

    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private JWTTokenService jwtTokenService;

    @Autowired
    public AuthService(
        UserRepository userRepository,
        BCryptPasswordEncoder bCryptPasswordEncoder,
        JWTTokenService jwtTokenService) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtTokenService = jwtTokenService;
    }

    public Mono<User> registrationUser(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        Collection<GrantedAuthority> grantedAuthorities = new LinkedList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority("USER"));
        user.setGrantedAuthorities(grantedAuthorities);
        user.setTapeType(TapeType.ALL.getText());
        return userRepository.save(user);
    }

    public Mono<Tuple2<String, String>> authorize(AuthDTO authDTO) {
        Mono<User> userMono = userRepository.findByEmail(authDTO.getEmail());
        return userMono
            .filter(user -> Objects.nonNull(authDTO.getPassword()))
            .filter(user -> bCryptPasswordEncoder.matches(authDTO.getPassword(), user.getPassword()))
            .map(this::generateToken)
            .zipWith(userMono.map(User::getId))
            ;
    }

    public Mono<RegistrationDTO> check(RegistrationDTO registrationDTO) {
        return Mono.just(registrationDTO)
            .filter(reg -> Objects.nonNull(reg.getEmail()) && !reg.getEmail().isEmpty())
            .switchIfEmpty(Mono.error(new DataIsIncorrectException("email")))
            .filter(reg -> reg.getEmail().matches("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$"))
            .switchIfEmpty(Mono.error(new DataIsIncorrectException("email")))
            .filter(reg -> Objects.nonNull(reg.getPassword()) && !reg.getPassword().isEmpty())
            .switchIfEmpty(Mono.error(new DataIsIncorrectException("password")))
            .filter(reg -> Objects.nonNull(reg.getName()) && !reg.getName().isEmpty())
            .switchIfEmpty(Mono.error(new DataIsIncorrectException("name")))
            .flatMap(reg -> userRepository.findByEmail(reg.getEmail())
                .flatMap(user1 -> Mono.just(user1)
                    .map(user -> new RegistrationDTO(user.getName(), user.getEmail(), user.getPassword()))
                    .filter(u -> !u.getEmail().equals(reg.getEmail()))
                    .switchIfEmpty(Mono.error(EmailBusyException::new))
                )
                .switchIfEmpty(Mono.just(reg)));
    }

    public String generateToken(User user) {
        String token = jwtTokenService.generateToken(
            user.getEmail(),
            null,
            user.getAuthorities());
        return String.join(" ", "Bearer", token);
    }
}