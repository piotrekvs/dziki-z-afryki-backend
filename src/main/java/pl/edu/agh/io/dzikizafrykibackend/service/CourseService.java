package pl.edu.agh.io.dzikizafrykibackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.io.dzikizafrykibackend.db.entity.CourseEntity;
import pl.edu.agh.io.dzikizafrykibackend.db.entity.DateEntity;
import pl.edu.agh.io.dzikizafrykibackend.db.entity.User;
import pl.edu.agh.io.dzikizafrykibackend.db.repository.CourseRepository;
import pl.edu.agh.io.dzikizafrykibackend.db.repository.DateRepository;
import pl.edu.agh.io.dzikizafrykibackend.db.repository.UserRepository;
import pl.edu.agh.io.dzikizafrykibackend.exception.CourseMissingException;
import pl.edu.agh.io.dzikizafrykibackend.exception.CourseNameMissingException;
import pl.edu.agh.io.dzikizafrykibackend.exception.InvalidOwnerException;
import pl.edu.agh.io.dzikizafrykibackend.model.Course;
import pl.edu.agh.io.dzikizafrykibackend.model.CourseUpdate;
import pl.edu.agh.io.dzikizafrykibackend.model.DateResource;
import pl.edu.agh.io.dzikizafrykibackend.util.CodeGenerator;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {
    private static final Set<User> EMPTY_USER_SET = Set.of();
    private static final Set<DateEntity> EMPTY_DATE_SET = Set.of();

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final DateRepository dateRepository;

    private final CodeGenerator codeGenerator;

    @Transactional
    public Course getCourse(UUID courseId) {
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
    public void deleteCourse(UserDetails userDetails, UUID courseId) {
        courseRepository.findById(courseId)
                .map(course -> validateOwner(course, userDetails))
                .orElseThrow(CourseMissingException::new);

        courseRepository.deleteById(courseId);
    }

    @Transactional
    public Course postCourse(UserDetails userDetails, CourseUpdate course) {
        if (course.getName().isEmpty()) {
            throw new CourseNameMissingException();
        }

        CourseEntity courseEntity = CourseEntity.builder()
                .name(course.getName().get())
                .desc(course.getDescription().orElse(null))
                .users(course.getUsers().map(this::usersFrom).orElse(EMPTY_USER_SET))
                .dates(course.getDates().map(this::datesFrom).orElse(EMPTY_DATE_SET))
                .ownerEmail(userDetails.getUsername())
                .courseCode(codeGenerator.generateCode())
                .build();

        courseRepository.save(courseEntity);

        return Course.fromEntity(courseEntity);
    }

    @Transactional
    public Course putCourse(UserDetails userDetails, UUID courseId, CourseUpdate update) {
        return courseRepository.findById(courseId)
                .map(course -> validateOwner(course, userDetails))
                .map(course -> updateCourse(course, update))
                .map(Course::fromEntity)
                .orElseThrow(CourseMissingException::new);
    }

    private CourseEntity validateOwner(CourseEntity courseEntity, UserDetails userDetails) {
        if (!Objects.equals(courseEntity.getOwnerEmail(), userDetails.getUsername())) {
            throw new InvalidOwnerException();
        }
        return courseEntity;
    }

    private CourseEntity updateCourse(CourseEntity entity, CourseUpdate update) {
        update.getName().ifPresent(entity::setName);
        update.getDescription().ifPresent(entity::setDesc);
        update.getUsers().map(this::usersFrom).ifPresent(entity::setUsers);
        update.getDates().map(this::datesFrom).ifPresent(entity::setDates);
        update.getOwnerEmail().ifPresent(entity::setOwnerEmail);
        courseRepository.save(entity);
        return entity;
    }

    private Set<User> usersFrom(Set<String> emails) {
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
