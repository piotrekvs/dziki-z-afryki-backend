package pl.edu.agh.io.dzikizafrykibackend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.io.dzikizafrykibackend.exception.CourseMissingException;
import pl.edu.agh.io.dzikizafrykibackend.model.Course;
import pl.edu.agh.io.dzikizafrykibackend.model.CourseList;
import pl.edu.agh.io.dzikizafrykibackend.model.CourseUpdate;
import pl.edu.agh.io.dzikizafrykibackend.service.CourseService;

import java.util.List;
import java.util.Set;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/course")
public class CourseController {
    private final CourseService courseService;

    @PostMapping
    private Course postCourse(String name, String desc, List<String> users) {
        return courseService.postCourse(name, desc, users);
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
    @GetMapping("/test")
    private Course getTestCourse() {
        return Course.builder()
                .name("Test name")
                .description("Test desc")
                .users(List.of("Kasia", "Basia"))
                .build();
    }
    @GetMapping
    private CourseList getAllCourses() {
        // return courseService.getAllCourses();
        return CourseList.builder()
                .courseList(courseService.getAllCourses())
                .build();
    }

    @DeleteMapping("/{courseId}")
    private void deleteCourse(@PathVariable int courseId) {
        courseService.deleteCourse(courseId);
    }
}
