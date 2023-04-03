package pl.edu.agh.io.dzikizafrykibackend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.io.dzikizafrykibackend.exception.CourseMissingException;
import pl.edu.agh.io.dzikizafrykibackend.model.Course;
import pl.edu.agh.io.dzikizafrykibackend.model.CourseUpdate;
import pl.edu.agh.io.dzikizafrykibackend.service.CourseService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/course")
public class CourseController {
    private final CourseService courseService;

    @GetMapping("/test")
    private Course getTestCourse() {
        return Course.builder()
                .name("Test name")
                .description("Test desc")
                .users(List.of("Kasia", "Basia"))
                .build();
    }

    @PostMapping
    private Course postCourse(@RequestBody CourseUpdate course) {
        return courseService.postCourse(course);
    }

    @PutMapping("/{courseId}")
    private Course putCourse(@PathVariable int courseId, @RequestBody CourseUpdate course) {
        return courseService.putCourse(courseId, course);
    }

    @GetMapping("/{courseId}")
    private Course getCourse(@PathVariable int courseId) {
        return courseService.getCourse(courseId)
                .orElseThrow(CourseMissingException::new);
    }

    @GetMapping
    private List<Course> getAllCourses() {
        return courseService.getAllCourses();
    }

    @DeleteMapping("/{courseId}")
    private void deleteCourse(@PathVariable int courseId) {
        courseService.deleteCourse(courseId);
    }
}
