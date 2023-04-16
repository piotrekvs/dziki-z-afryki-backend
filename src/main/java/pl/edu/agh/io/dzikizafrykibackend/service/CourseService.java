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
import pl.edu.agh.io.dzikizafrykibackend.exception.CourseMissingException;
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
    public Course getCourse(int courseId) {
        Optional<CourseEntity> course = courseRepository.findById(courseId);
        return course.map(Course::fromEntity).orElseThrow(CourseMissingException::new);
    }

    @Transactional
    public List<Course> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(Course::fromEntity)
                .toList();
    }

    @Transactional
    public void deleteCourse(int courseId) {
        if (courseRepository.existsById(courseId)) {
            courseRepository.deleteById(courseId);
        } else {
            throw new CourseMissingException();
        }
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
                .owner(this.usersFrom(Set.of(course.getOwner().get())).stream().findFirst().get())
                .code(course.getCode().orElse(null))
                .build();

        courseRepository.save(courseEntity);

        return Course.fromEntity(courseEntity);
    }

    @Transactional
    public Course putCourse(int courseId, CourseUpdate update) {
        return courseRepository.findById(courseId)
                .map(entity -> updateCourse(entity, update))
                .map(Course::fromEntity)
                .orElseThrow(CourseMissingException::new);
    }

    private CourseEntity updateCourse(CourseEntity entity, CourseUpdate update) {
        update.getName().ifPresent(entity::setName);
        update.getUsers().map(this::usersFrom).ifPresent(entity::setUsers);
        update.getDates().map(this::datesFrom).ifPresent(entity::setDates);
        courseRepository.save(entity);
        return entity;
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
