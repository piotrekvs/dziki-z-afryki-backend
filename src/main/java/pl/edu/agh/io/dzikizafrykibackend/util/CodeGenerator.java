package pl.edu.agh.io.dzikizafrykibackend.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.edu.agh.io.dzikizafrykibackend.db.repository.CourseRepository;

import static org.apache.commons.lang3.RandomStringUtils.random;

@Component
@RequiredArgsConstructor
public class CodeGenerator {
    private final CourseRepository courseRepository;

    public String generateCode() {
        String code = random(8, true, true);

        while (courseRepository.findFirstByCourseCodeEquals(code) != null) {
            code = random(8, true, true);
        }

        return code;
    }
}
