package pl.edu.agh.io.dzikizafrykibackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.io.dzikizafrykibackend.db.entity.CourseEntity;
import pl.edu.agh.io.dzikizafrykibackend.db.entity.DateEntity;
import pl.edu.agh.io.dzikizafrykibackend.db.entity.UserEntity;
import pl.edu.agh.io.dzikizafrykibackend.db.repository.CourseRepository;
import pl.edu.agh.io.dzikizafrykibackend.db.repository.DateRepository;
import pl.edu.agh.io.dzikizafrykibackend.db.repository.UserRepository;
import pl.edu.agh.io.dzikizafrykibackend.exception.CourseNameMissingException;
import pl.edu.agh.io.dzikizafrykibackend.model.Course;
import pl.edu.agh.io.dzikizafrykibackend.model.CourseUpdate;
import pl.edu.agh.io.dzikizafrykibackend.model.DateResource;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {
    private static final Set<UserEntity> EMPTY_USER_SET = Set.of();
    private static final Set<DateEntity> EMPTY_DATE_SET = Set.of();

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final DateRepository dateRepository;

    @Transactional
    public Optional<Course> getCourse(int courseId) {
        Optional<CourseEntity> course = courseRepository.findById(courseId);
        return course.map(Course::fromEntity);
    }

    @Transactional
    public List<Course> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(Course::fromEntity)
                .toList();
    }

    @Transactional
    public void deleteCourse(int courseId) {
        courseRepository.deleteById(courseId);
    }

    @Transactional
    public Course postCourse(CourseUpdate course) {
        if (course.getName().isEmpty()) {
            throw new CourseNameMissingException();
        }

        CourseEntity courseEntity = CourseEntity.builder()
                .name(course.getName().get())
                .desc(course.getDescription().orElse(null))
                .users(course.getUsers().map(this::usersFrom).orElse(EMPTY_USER_SET))
                .dates(course.getDates().map(this::datesFrom).orElse(EMPTY_DATE_SET))
                .build();

        courseRepository.save(courseEntity);

        return Course.fromEntity(courseEntity);
    }

    @Transactional
    public Course putCourse(int courseId, CourseUpdate course) {
        return courseRepository.findById(courseId)
                .map(entity -> updateCourse(entity, course))
                .map(Course::fromEntity)
                .orElseGet(() -> postCourse(courseId, course));
    }

    private CourseEntity updateCourse(CourseEntity entity, CourseUpdate update) {
        update.getName().ifPresent(entity::setName);
        update.getUsers().map(this::usersFrom).ifPresent(entity::setUsers);
        update.getDates().map(this::datesFrom).ifPresent(entity::setDates);
        courseRepository.save(entity);
        return entity;
    }

    private Course postCourse(int courseId, CourseUpdate course) {
        if (course.getName().isEmpty()) {
            throw new CourseNameMissingException();
        }
        CourseEntity courseEntity = CourseEntity.builder()
                .id(courseId)
                .name(course.getName().get())
                .desc(course.getDescription().orElse(null))
                .users(course.getUsers().map(this::usersFrom).orElse(EMPTY_USER_SET))
                .dates(course.getDates().map(this::datesFrom).orElse(EMPTY_DATE_SET))
                .build();

        courseRepository.save(courseEntity);

        return Course.fromEntity(courseEntity);
    }

    private Set<UserEntity> usersFrom(Set<String> emails) {
        return emails.stream()
                .map(userRepository::findByEmail)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
    }

    private Set<DateEntity> datesFrom(Set<DateResource> resources) {
        return resources.stream()
                .map(this::dateFrom)
                .collect(Collectors.toSet());
    }

    private DateEntity dateFrom(DateResource resource) {
        DateEntity entity = DateEntity.builder()
                .weekDay(resource.getWeekDay())
                .startTime(resource.getStartTime())
                .endTime(resource.getEndTime())
                .build();
        return dateRepository.save(entity);
    }
}
