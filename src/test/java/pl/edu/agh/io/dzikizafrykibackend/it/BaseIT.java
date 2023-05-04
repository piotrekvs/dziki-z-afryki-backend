package pl.edu.agh.io.dzikizafrykibackend.it;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.lifecycle.Startables;
import pl.edu.agh.io.dzikizafrykibackend.BackendApplication;
import pl.edu.agh.io.dzikizafrykibackend.db.entity.UserRole;
import pl.edu.agh.io.dzikizafrykibackend.db.repository.CourseRepository;
import pl.edu.agh.io.dzikizafrykibackend.db.repository.UserRepository;
import pl.edu.agh.io.dzikizafrykibackend.it.client.RetrofitClient;
import pl.edu.agh.io.dzikizafrykibackend.it.dsl.IdentityProviderDsl;
import pl.edu.agh.io.dzikizafrykibackend.it.dsl.TestDsl;

@SpringBootTest(
        classes = {BackendApplication.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class BaseIT {

    protected static final IdentityProviderDsl.UserIdentity STUDENT1_ID = new IdentityProviderDsl.UserIdentity(
            "student1@example.com", "student1_passwd", UserRole.STUDENT
    );
    protected static final IdentityProviderDsl.UserIdentity STUDENT2_ID = new IdentityProviderDsl.UserIdentity(
            "student2@example.com", "student2_passwd", UserRole.STUDENT
    );
    protected static final IdentityProviderDsl.UserIdentity TEACHER1_ID = new IdentityProviderDsl.UserIdentity(
            "teacher@example.com", "teacher_passwd", UserRole.TEACHER
    );

    private final static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15");

    @Value("${local.server.port}")
    protected int port;

    @Autowired
    private RetrofitClient retrofitClient;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CourseRepository courseRepository;

    @DynamicPropertySource
    public static void setupThings(DynamicPropertyRegistry registry) {
        Startables.deepStart(postgreSQLContainer).join();

        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    protected TestDsl dsl() {
        return new TestDsl(
                port,
                userRepository,
                courseRepository
        );
    }

    protected RetrofitClient retrofitClient() {
        return retrofitClient;
    }
}
