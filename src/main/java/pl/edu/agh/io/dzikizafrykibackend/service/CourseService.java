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
import java.util.Set;

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

    public Course postCourse(String name, String desc, List<String> users) {
        CourseEntity course = CourseEntity.builder()
                .name(name)
                .desc(desc)
                .users(users)
                .build();
        courseRepository.save(course);

        return Course.fromEntity(course);
    }

    public Course putCourse(int courseId, CourseUpdate course) {
        return courseRepository.findById(courseId)
                .map(entity -> updateCourse(entity, course))
                .map(Course::fromEntity)
                .orElseGet(() -> postCourse(course));
    }

    private CourseEntity updateCourse(CourseEntity entity, CourseUpdate update) {
        update.getName().ifPresent(entity::setName);
        update.getUsers().ifPresent(entity::setUsers);
        courseRepository.save(entity);
        return entity;
    }

    private Course postCourse(CourseUpdate course) {
        // for now only course name is mandatory
        if(course.getName().isEmpty()) {
            throw new CourseNameMissingException();
        }
        return postCourse(course.getName().get(), course.getDescription().orElse(""), course.getUsers().orElse(List.of()));
    }

    public void deleteCourse(int courseId) {
        courseRepository.deleteById(courseId);
    }
}
