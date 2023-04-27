package pl.edu.agh.io.dzikizafrykibackend.it;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.edu.agh.io.dzikizafrykibackend.db.entity.User;
import pl.edu.agh.io.dzikizafrykibackend.model.AuthenticationRequestResource;
import pl.edu.agh.io.dzikizafrykibackend.model.AuthenticationResponseResource;
import pl.edu.agh.io.dzikizafrykibackend.model.RegisterRequestResource;

import java.io.IOException;

public class AuthenticationIT extends BaseIT {

    @BeforeEach
    public void setup() {
        dsl().purgeEntities();
    }

    @Test
    void shouldRegisterAsTeacher() throws IOException {
        // given
        RegisterRequestResource registerRequestResource = RegisterRequestResource.builder()
                .firstName("Jan")
                .lastName("Kowalski")
                .indexNumber(null)
                .email(STUDENT1_ID.email())
                .password(STUDENT1_ID.password())
                .role(STUDENT1_ID.role())
                .build();

        // when
        AuthenticationResponseResource authenticationResponseResource =
                retrofitClient().postRegister(registerRequestResource).execute().body();

        // then
        Assertions.assertThat(authenticationResponseResource).isNotNull();
        Assertions.assertThat(authenticationResponseResource.jwt).isNotEmpty();

        User user = dsl().getUserRepository().findByEmail(STUDENT1_ID.email()).orElse(null);
        Assertions.assertThat(user).isNotNull();
        Assertions.assertThat(user.getEmail()).isEqualTo(STUDENT1_ID.email());
    }

    @Test
    void shouldAuthenticateAlreadyCreatedUser() throws IOException {
        // given
        dsl().setupUser(STUDENT1_ID);

        // when
        AuthenticationRequestResource authenticationRequestResource = AuthenticationRequestResource.builder()
                .email(STUDENT1_ID.email())
                .password(STUDENT1_ID.password())
                .build();

        AuthenticationResponseResource authenticationResponseResource =
                retrofitClient().postAuthenticate(authenticationRequestResource).execute().body();

        Assertions.assertThat(authenticationResponseResource).isNotNull();
        Assertions.assertThat(authenticationResponseResource.jwt).isNotEmpty();

        User user = dsl().getUserRepository().findByEmail(STUDENT1_ID.email()).orElse(null);
        Assertions.assertThat(user).isNotNull();
        Assertions.assertThat(user.getEmail()).isEqualTo(STUDENT1_ID.email());
    }


}
