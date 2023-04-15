package pl.edu.agh.io.dzikizafrykibackend.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.io.dzikizafrykibackend.db.entity.UserRole;
import pl.edu.agh.io.dzikizafrykibackend.exception.CourseMissingException;
import pl.edu.agh.io.dzikizafrykibackend.model.Course;
import pl.edu.agh.io.dzikizafrykibackend.model.CourseUpdate;
import pl.edu.agh.io.dzikizafrykibackend.service.CourseService;

import java.util.List;

@RestController
@RequestMapping("/course")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;

    @GetMapping("/test")
    public Course getTestCourse() {
        return Course.builder()
                .name("Test name")
                .description("Test desc")
                .build();
    }

    @GetMapping("/{courseId}")
    @Secured({UserRole.ROLE_TEACHER, UserRole.ROLE_STUDENT})
    public Course getCourse(@PathVariable int courseId) {
        return courseService.getCourse(courseId)
                .orElseThrow(CourseMissingException::new);
    }

    @GetMapping
    @Secured({UserRole.ROLE_TEACHER, UserRole.ROLE_STUDENT})
    public List<Course> getAllCourses() {
        return courseService.getAllCourses();
    }

    @PostMapping
    @Secured({UserRole.ROLE_TEACHER})
    public Course postCourse(@RequestBody CourseUpdate course) {
        return courseService.postCourse(course);
    }

    @PutMapping("/{courseId}")
    @Secured({UserRole.ROLE_TEACHER})
    public Course putCourse(@PathVariable int courseId, @RequestBody CourseUpdate course) {
        return courseService.putCourse(courseId, course);
    }

    @DeleteMapping("/{courseId}")
    @Secured({UserRole.ROLE_TEACHER})
    public void deleteCourse(@PathVariable int courseId) {
        courseService.deleteCourse(courseId);
    }
}
