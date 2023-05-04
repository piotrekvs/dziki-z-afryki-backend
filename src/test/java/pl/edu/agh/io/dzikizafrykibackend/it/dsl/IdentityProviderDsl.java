package pl.edu.agh.io.dzikizafrykibackend.it.dsl;

import lombok.Builder;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomUtils;
import pl.edu.agh.io.dzikizafrykibackend.db.entity.UserRole;
import pl.edu.agh.io.dzikizafrykibackend.db.repository.CourseRepository;
import pl.edu.agh.io.dzikizafrykibackend.db.repository.UserRepository;
import pl.edu.agh.io.dzikizafrykibackend.it.client.RetrofitClient;
import pl.edu.agh.io.dzikizafrykibackend.model.AuthenticationRequestResource;
import pl.edu.agh.io.dzikizafrykibackend.model.RegisterRequestResource;

import java.io.IOException;

@Builder
public class IdentityProviderDsl {

    private final UserRepository userRepository;

    private final CourseRepository courseRepository;

    private final RetrofitClient retrofitClient;

    public IdentityProviderDsl(UserRepository userRepository, CourseRepository courseRepository, RetrofitClient retrofitClient) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.retrofitClient = retrofitClient;
    }

    public String register(UserIdentity userIdentity) throws IOException {
        RegisterRequestResource registerRequestResource = RegisterRequestResource.builder()
                .firstName(RandomStringUtils.randomAlphabetic(5))
                .lastName(RandomStringUtils.randomAlphabetic(5))
                .indexNumber(userIdentity.role == UserRole.STUDENT ? RandomUtils.nextInt(0, 999999) : null)
                .email(userIdentity.email)
                .password(userIdentity.password)
                .role(userIdentity.role)
                .build();

        return retrofitClient.postRegister(registerRequestResource).execute().body().jwt;
    }

    public String authenticate(UserIdentity userIdentity) throws IOException {
        AuthenticationRequestResource authenticationRequestResource = AuthenticationRequestResource.builder()
                .email(userIdentity.email)
                .password(userIdentity.password)
                .build();

        return retrofitClient.postAuthenticate(authenticationRequestResource).execute().body().jwt;
    }

    public record UserIdentity(String email, String password, UserRole role) {}
}
