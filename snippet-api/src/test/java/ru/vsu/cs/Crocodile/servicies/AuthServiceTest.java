package ru.vsu.cs.Crocodile.servicies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.vsu.cs.Crocodile.CustomException.DataIsIncorrectException;
import ru.vsu.cs.Crocodile.CustomException.EmailBusyException;
import ru.vsu.cs.Crocodile.DTO.AuthDTO;
import ru.vsu.cs.Crocodile.DTO.RegistrationDTO;
import ru.vsu.cs.Crocodile.entities.User;
import ru.vsu.cs.Crocodile.jwt.JWTTokenService;
import ru.vsu.cs.Crocodile.repositories.UserRepository;

import static org.mockito.Mockito.when;

class AuthServiceTest {

    private final Logger LOG = LoggerFactory.getLogger(AuthServiceTest.class);

    private UserRepository userRepository;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        LOG.debug("setup");
        userRepository = Mockito.mock(UserRepository.class);
        BCryptPasswordEncoder bCryptPasswordEncoder = Mockito.mock(BCryptPasswordEncoder.class);
        JWTTokenService jwtTokenService = Mockito.mock(JWTTokenService.class);

        authService = new AuthService(userRepository, bCryptPasswordEncoder, jwtTokenService);
    }

    @Test
    void authorizationOfUserNullFields() {
        RegistrationDTO authDTO = new RegistrationDTO();
        LOG.debug("authDTO : {}", authDTO);
        StepVerifier.create(authService.check(authDTO))
                .expectError(DataIsIncorrectException.class)
                .verify();
    }

    @Test
    void authorizationOfUserWithoutName() {
        RegistrationDTO authDTO = new RegistrationDTO();
        authDTO.setEmail("df@ds");
        authDTO.setName("");
        authDTO.setPassword("pass");
        LOG.debug("authDTO : {}", authDTO);
        StepVerifier.create(authService.check(authDTO))
                .expectError(DataIsIncorrectException.class)
                .verify();
    }

    @Test
    void authorizationOfUserWithoutEmail() {
        RegistrationDTO authDTO = new RegistrationDTO();
        authDTO.setEmail("");
        authDTO.setName("ds");
        authDTO.setPassword("pass");
        StepVerifier.create(authService.check(authDTO))
                .expectError(DataIsIncorrectException.class)
                .verify();
    }

    @Test
    void authorizationOfUserWithoutPassword() {
        RegistrationDTO authDTO = new RegistrationDTO();
        authDTO.setEmail("dsac@we.ds");
        authDTO.setName("ds");
        authDTO.setPassword("");
        StepVerifier.create(authService.check(authDTO))
                .expectError(DataIsIncorrectException.class)
                .verify();
    }
}