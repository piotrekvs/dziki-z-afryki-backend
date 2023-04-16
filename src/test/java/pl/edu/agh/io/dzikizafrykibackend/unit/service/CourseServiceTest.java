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
import pl.edu.agh.io.dzikizafrykibackend.db.entity.UserEntity;
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
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CourseServiceTest {
    private static final String COURSE_NAME = "test";
    private static final String COURSE_NAME_2 = "test2";
    private static final String COURSE_CODE = "code";
    private static final int COURSE_ID = 5;
    private static final UserEntity COURSE_OWNER = new UserEntity("aaa@bb.cc", "aaa", "bbb", UserRole.TEACHER, null, true, "aaa");

    private static final Set<UserEntity> EMPTY_USER_SET = Set.of();
    private static final Set<DateEntity> EMPTY_DATE_SET = Set.of();

    @Mock
    private CourseRepository courseRepositoryMock;
    @Mock
    private UserRepository userRepositoryMock;
    @Mock
    private DateRepository dateRepositoryMock;
    @InjectMocks
    private CourseService courseService;

    @Test
    void shouldGetCourse() {
        // given
        when(courseRepositoryMock.findById(COURSE_ID))
                .thenReturn(Optional.of(new CourseEntity(COURSE_ID, COURSE_NAME, "", EMPTY_USER_SET, EMPTY_DATE_SET, COURSE_OWNER, COURSE_CODE)));

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
        when(courseRepositoryMock.existsById(COURSE_ID)).thenReturn(true);

        // when
        courseService.deleteCourse(COURSE_ID);

        // then
        verify(courseRepositoryMock).deleteById(COURSE_ID);
    }

    @Test
    void shouldThrowWhenDeleteNonExistentCourse() {
        // given
        when(courseRepositoryMock.existsById(COURSE_ID)).thenReturn(false);

        // when
        // then
        Assertions.assertThrows(CourseMissingException.class, () -> courseService.deleteCourse(COURSE_ID));
        verify(courseRepositoryMock, never()).deleteById(COURSE_ID);
    }

    @ParameterizedTest
    @MethodSource("providerForPostTest")
    void shouldPostCourse(CourseUpdate request, CourseEntity expected) {
        // given
        for (DateEntity date : expected.getDates()) {
            when(dateRepositoryMock.save(date)).thenReturn(date);
        }

        when(userRepositoryMock.findByEmail(COURSE_OWNER.getEmail())).thenReturn(Optional.of(COURSE_OWNER));

        // when
        courseService.postCourse(request);

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
        Assertions.assertThrows(CourseNameMissingException.class, () -> courseService.postCourse(request));

        // then
        verifyNoInteractions(courseRepositoryMock);
    }

    @Test
    void shouldPutCourseOnExistingCourse() {
        // given
        CourseEntity courseEntity = CourseEntity.builder()
                .id(COURSE_ID)
                .name(COURSE_NAME)
                .users(EMPTY_USER_SET)
                .dates(EMPTY_DATE_SET)
                .owner(COURSE_OWNER)
                .code(COURSE_CODE)
                .build();

        CourseUpdate courseUpdate = CourseUpdate.builder()
                .name(Optional.of(COURSE_NAME_2))
                .build();

        CourseEntity updatedCourseEntity = courseEntity.toBuilder()
                .name(COURSE_NAME_2)
                .build();

        when(courseRepositoryMock.findById(COURSE_ID)).thenReturn(Optional.of(courseEntity));

        // when
        Course updatedCourse = courseService.putCourse(COURSE_ID, courseUpdate);

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

        when(courseRepositoryMock.findById(COURSE_ID)).thenReturn(Optional.empty());

        // then
        Assertions.assertThrows(CourseMissingException.class, () -> courseService.putCourse(COURSE_ID, courseUpdate));
    }

    private static Stream<Arguments> providerForPostTest() {
        return Stream.of(
                Arguments.of(CourseUpdate.builder()
                                     .name(Optional.of(COURSE_NAME))
                                     .owner(Optional.of(COURSE_OWNER.getEmail()))
                                     .code(Optional.of(COURSE_CODE))
                                     .build(),
                             CourseEntity.builder()
                                     .name(COURSE_NAME)
                                     .dates(Set.of())
                                     .users(Set.of())
                                     .code(COURSE_CODE)
                                     .owner(COURSE_OWNER)
                                     .build())
        );
    }

    private InOrder getIo() {
        return inOrder(dateRepositoryMock, courseRepositoryMock, userRepositoryMock);
    }

}
