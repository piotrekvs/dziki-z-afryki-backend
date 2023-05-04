package pl.edu.agh.io.dzikizafrykibackend.it;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.edu.agh.io.dzikizafrykibackend.model.Course;
import pl.edu.agh.io.dzikizafrykibackend.model.CourseUpdate;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

public class CourseIT extends BaseIT {

    @BeforeEach
    public void setup() throws IOException {
        dsl().purgeEntities();
        dsl().setupUser(TEACHER1_ID);
    }

    @Test
    void shouldDeleteCreatedCourse() throws IOException {
        // given
        CourseUpdate courseUpdate = CourseUpdate.builder()
                .name(Optional.of("TEST_NAME"))
                .build();

        // when
        Course course = dsl().useClientAsUser(TEACHER1_ID).postCourse(courseUpdate).execute().body();

        Assertions.assertThat(course).isNotNull();

        UUID courseId = course.getCourseId();
        dsl().useClientAsUser(TEACHER1_ID).deleteCourse(courseId).execute();

        Assertions.assertThat(dsl().getCourseRepository().findById(courseId)).isEmpty();
    }
}
