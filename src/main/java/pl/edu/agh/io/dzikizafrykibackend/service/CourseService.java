package pl.edu.agh.io.dzikizafrykibackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.agh.io.dzikizafrykibackend.db.entity.CourseEntity;
import pl.edu.agh.io.dzikizafrykibackend.db.repository.CourseRepository;
import pl.edu.agh.io.dzikizafrykibackend.exception.CourseNameMissingException;
import pl.edu.agh.io.dzikizafrykibackend.model.Course;
import pl.edu.agh.io.dzikizafrykibackend.model.CourseUpdate;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;

    public Optional<Course> getCourse(int courseId) {
        Optional<CourseEntity> course = courseRepository.findById(courseId);
        return course.map(Course::fromEntity);
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(Course::fromEntity)
                .toList();
    }

    public void deleteCourse(int courseId) {
        courseRepository.deleteById(courseId);
    }

    public Course postCourse(CourseUpdate course) {
        if (course.getName().isEmpty()) {
            throw new CourseNameMissingException();
        }
        CourseEntity courseEntity = CourseEntity.builder()
                .name(course.getName().get())
                .desc(course.getDescription().orElse(null))
                .users(course.getUsers().orElse(null))
                .build();
        courseRepository.save(courseEntity);

        return Course.fromEntity(courseEntity);
    }

    public Course putCourse(int courseId, CourseUpdate course) {
        return courseRepository.findById(courseId)
                .map(entity -> updateCourse(entity, course))
                .map(Course::fromEntity)
                .orElseGet(() -> postCourse(courseId, course));
    }

    private CourseEntity updateCourse(CourseEntity entity, CourseUpdate update) {
        update.getName().ifPresent(entity::setName);
        update.getUsers().ifPresent(entity::setUsers);
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
                .users(course.getUsers().orElse(null))
                .build();
        courseRepository.save(courseEntity);

        return Course.fromEntity(courseEntity);
    }
}
