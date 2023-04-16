package pl.edu.agh.io.dzikizafrykibackend.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.edu.agh.io.dzikizafrykibackend.db.entity.UserEntity;
import pl.edu.agh.io.dzikizafrykibackend.db.entity.UserRole;
import pl.edu.agh.io.dzikizafrykibackend.db.repository.UserRepository;
import pl.edu.agh.io.dzikizafrykibackend.exception.DuplicateUserException;
import pl.edu.agh.io.dzikizafrykibackend.model.AuthenticationRequestResource;
import pl.edu.agh.io.dzikizafrykibackend.model.AuthenticationResponseResource;
import pl.edu.agh.io.dzikizafrykibackend.model.RegisterRequestResource;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthenticationResponseResource register(RegisterRequestResource registerRequestResource) {
        UserEntity newUser = new UserEntity(registerRequestResource.getEmail(),
                                            registerRequestResource.getFirstName(),
                                            registerRequestResource.getLastName(),
                                            registerRequestResource.getRole(),
                                            registerRequestResource.getIndexNumber(),
                                            true,
                                            passwordEncoder.encode(registerRequestResource.getPassword()));
        if (userRepository.findByEmail(newUser.getEmail()).isPresent()) {
            throw new DuplicateUserException();
        }
        userRepository.save(newUser);
        return new AuthenticationResponseResource(jwtService.generateToken(newUser));
    }

    public AuthenticationResponseResource authenticate(AuthenticationRequestResource authenticationRequestResource) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                        authenticationRequestResource.getEmail(),
                        authenticationRequestResource.getPassword()
                )
        );
        UserEntity user = userRepository.findByEmail(authenticationRequestResource.getEmail()).orElseThrow();
        return new AuthenticationResponseResource(jwtService.generateToken(user));
    }
}
