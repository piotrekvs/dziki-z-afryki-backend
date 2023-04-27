package pl.edu.agh.io.dzikizafrykibackend.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.io.dzikizafrykibackend.db.entity.UserRole;
import pl.edu.agh.io.dzikizafrykibackend.model.Course;
import pl.edu.agh.io.dzikizafrykibackend.model.CourseUpdate;
import pl.edu.agh.io.dzikizafrykibackend.service.CourseService;

import java.util.List;
import java.util.UUID;

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
    public Course getCourse(@PathVariable UUID courseId) {
        return courseService.getCourse(courseId);
    }

    @GetMapping
    @Secured({UserRole.ROLE_TEACHER, UserRole.ROLE_STUDENT})
    public List<Course> getAllCourses() {
        return courseService.getAllCourses();
    }

    @PostMapping
    @Secured({UserRole.ROLE_TEACHER})
    public Course postCourse(Authentication authentication, @RequestBody CourseUpdate course) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        System.out.println(userDetails.toString());
        return courseService.postCourse(userDetails, course);
    }

    @PutMapping("/{courseId}")
    @Secured({UserRole.ROLE_TEACHER})
    public Course putCourse(Authentication authentication, @PathVariable UUID courseId, @RequestBody CourseUpdate course) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return courseService.putCourse(userDetails, courseId, course);
    }

    @DeleteMapping("/{courseId}")
    @Secured({UserRole.ROLE_TEACHER})
    public void deleteCourse(Authentication authentication, @PathVariable UUID courseId) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        courseService.deleteCourse(userDetails, courseId);
    }
}
