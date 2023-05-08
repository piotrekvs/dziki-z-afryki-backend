package pl.edu.agh.io.dzikizafrykibackend.unit.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.edu.agh.io.dzikizafrykibackend.db.entity.CourseEntity;
import pl.edu.agh.io.dzikizafrykibackend.db.entity.DateEntity;
import pl.edu.agh.io.dzikizafrykibackend.db.entity.User;
import pl.edu.agh.io.dzikizafrykibackend.db.entity.UserRole;
import pl.edu.agh.io.dzikizafrykibackend.db.repository.CourseRepository;
import pl.edu.agh.io.dzikizafrykibackend.db.repository.DateRepository;
import pl.edu.agh.io.dzikizafrykibackend.db.repository.UserRepository;
import pl.edu.agh.io.dzikizafrykibackend.exception.CourseMissingException;
import pl.edu.agh.io.dzikizafrykibackend.exception.CourseNameMissingException;
import pl.edu.agh.io.dzikizafrykibackend.model.Course;
import pl.edu.agh.io.dzikizafrykibackend.model.CourseUpdate;
import pl.edu.agh.io.dzikizafrykibackend.service.CourseService;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CourseServiceTest {
    private static final String COURSE_NAME = "test";
    private static final String COURSE_NAME_2 = "test2";
    private static final UUID COURSE_ID = UUID.randomUUID();
    private static final User TEACHER = new User("aaa@bb.cc", "aaa", "bbb", UserRole.TEACHER, null, true, "aaa");
    private static final CourseEntity COURSE_ENTITY = CourseEntity.builder()
            .id(COURSE_ID)
            .name(COURSE_NAME)
            .teacher(TEACHER)
            .build();
    private static final Set<User> EMPTY_STUDENTS_SET = Set.of();
    private static final Set<DateEntity> EMPTY_DATE_SET = Set.of();

    @Mock
    private CourseRepository courseRepositoryMock;
    @Mock
    private UserRepository userRepositoryMock;
    @Mock
    private DateRepository dateRepositoryMock;

    @InjectMocks
    private CourseService courseService;

    private static Stream<Arguments> providerForPostTest() {
        return Stream.of(
                Arguments.of(CourseUpdate.builder()
                                     .name(Optional.of(COURSE_NAME))
                                     .description(Optional.of("description"))
                                     .ownerEmail(Optional.of(TEACHER.getEmail()))
                                     .build(),
                             CourseEntity.builder()
                                     .name(COURSE_NAME)
                                     .description("description")
                                     .teacher(TEACHER)
                                     .dates(Set.of())
                                     .students(Set.of())
                                     .build())
        );
    }

    @Test
    void shouldGetCourse() {
        // given
        when(courseRepositoryMock.findById(COURSE_ID)).thenReturn(Optional.of(
                new CourseEntity(COURSE_ID, COURSE_NAME, "", TEACHER, EMPTY_STUDENTS_SET, EMPTY_DATE_SET)
        ));

        // when
        courseService.getCourse(COURSE_ID);

        // then
        verify(courseRepositoryMock).findById(COURSE_ID);
    }

    @Test
    void shouldThrowWhenGetCourseOnNonExistentCourse() {
        // given
        when(courseRepositoryMock.findById(COURSE_ID)).thenReturn(Optional.empty());

        // when
        // then
        Assertions.assertThrows(CourseMissingException.class, () -> courseService.getCourse(COURSE_ID));
        verify(courseRepositoryMock).findById(COURSE_ID);
    }

    @Test
    void shouldGetAllCourses() {
        // given

        // when
        courseService.getAllCourses();

        // then
        verify(courseRepositoryMock).findAll();
    }

    @Test
    void shouldDeleteCourse() {
        // given
        when(courseRepositoryMock.findById(COURSE_ID)).thenReturn(Optional.ofNullable(COURSE_ENTITY));
        when(userRepositoryMock.findByEmail(TEACHER.getEmail())).thenReturn(Optional.of(TEACHER));

        // when
        courseService.deleteCourse(TEACHER, COURSE_ID);

        // then
        verify(courseRepositoryMock).deleteById(COURSE_ID);
    }

    @Test
    void shouldThrowWhenDeleteNonExistentCourse() {
        // given
        // when
        // then
        Assertions.assertThrows(CourseMissingException.class, () -> courseService.deleteCourse(TEACHER, COURSE_ID));
        verify(courseRepositoryMock, never()).deleteById(COURSE_ID);
    }

    @ParameterizedTest
    @MethodSource("providerForPostTest")
    void shouldPostCourse(CourseUpdate request, CourseEntity expected) {
        // given
        for (DateEntity date : expected.getDates()) {
            when(dateRepositoryMock.save(date)).thenReturn(date);
        }
        when(userRepositoryMock.findByEmail(TEACHER.getEmail())).thenReturn(Optional.of(TEACHER));
        when(courseRepositoryMock.save(expected)).thenReturn(expected);

        // when
        courseService.postCourse(TEACHER, request);

        // then
        InOrder io = getIo();
        for (DateEntity date : expected.getDates()) {
            io.verify(dateRepositoryMock).save(date);
        }
        io.verify(courseRepositoryMock).save(expected);
        io.verifyNoMoreInteractions();
    }

    @Test
    void shouldThrowWhenPostCourseWithoutName() {
        // given
        CourseUpdate request = CourseUpdate.builder()
                .name(Optional.empty())
                .build();

        // when
        Assertions.assertThrows(CourseNameMissingException.class, () -> courseService.postCourse(TEACHER, request));

        // then
        verifyNoInteractions(courseRepositoryMock);
    }

    @Test
    void shouldPutCourseOnExistingCourse() {
        // given
        CourseEntity courseEntity = CourseEntity.builder()
                .id(COURSE_ID)
                .name(COURSE_NAME)
                .description("")
                .teacher(TEACHER)
                .students(EMPTY_STUDENTS_SET)
                .dates(EMPTY_DATE_SET)
                .build();

        CourseUpdate courseUpdate = CourseUpdate.builder()
                .name(Optional.of(COURSE_NAME_2))
                .build();

        CourseEntity updatedCourseEntity = courseEntity.toBuilder()
                .name(COURSE_NAME_2)
                .build();

        when(courseRepositoryMock.findById(COURSE_ID)).thenReturn(Optional.of(courseEntity));
        when(userRepositoryMock.findByEmail(TEACHER.getEmail())).thenReturn(Optional.of(TEACHER));

        // when
        Course updatedCourse = courseService.putCourse(TEACHER, COURSE_ID, courseUpdate);

        // then
        verify(courseRepositoryMock).findById(COURSE_ID);
        verify(courseRepositoryMock).save(updatedCourseEntity);

        Assertions.assertEquals(COURSE_ID, updatedCourse.getCourseId());
        Assertions.assertEquals(COURSE_NAME_2, updatedCourse.getName());
    }

    @Test
    void shouldThrowWhenPutCourseOnNonexistentCourse() {
        // given
        CourseUpdate courseUpdate = CourseUpdate.builder()
                .name(Optional.of(COURSE_NAME))
                .build();

        // then
        Assertions.assertThrows(CourseMissingException.class,
                                () -> courseService.putCourse(TEACHER, COURSE_ID, courseUpdate));
    }

    private InOrder getIo() {
        return inOrder(dateRepositoryMock, courseRepositoryMock, userRepositoryMock);
    }

}
