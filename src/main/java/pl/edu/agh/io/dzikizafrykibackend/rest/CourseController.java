package pl.edu.agh.io.dzikizafrykibackend.rest;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.io.dzikizafrykibackend.db.entity.User;
import pl.edu.agh.io.dzikizafrykibackend.db.entity.UserRole;
import pl.edu.agh.io.dzikizafrykibackend.model.Course;
import pl.edu.agh.io.dzikizafrykibackend.model.CourseCreationResource;
import pl.edu.agh.io.dzikizafrykibackend.model.CourseEnrollResource;
import pl.edu.agh.io.dzikizafrykibackend.model.UserPreferenceResource;
import pl.edu.agh.io.dzikizafrykibackend.service.CourseService;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/course")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping("/{courseId}")
    @Secured({UserRole.ROLE_TEACHER, UserRole.ROLE_STUDENT})
    public Course getCourse(@PathVariable UUID courseId) {
        return courseService.getCourse(courseId);
    }

    @GetMapping
    @Secured({UserRole.ROLE_TEACHER})
    public List<Course> getOwnedCourses(Authentication authentication) {
        User userContext = (User) authentication.getPrincipal();
        return courseService.getOwnedCourses(userContext);
    }

    @PostMapping(value = "/create", consumes = "application/json", produces = "application/json")
    @Secured({UserRole.ROLE_TEACHER})
    public Course postCourse(
            Authentication authentication,
            @Valid @RequestBody CourseCreationResource courseCreationResource
    ) {
        User userContext = (User) authentication.getPrincipal();
        return courseService.postCourse(userContext, courseCreationResource);
    }

    @DeleteMapping("/{courseId}")
    @Secured({UserRole.ROLE_TEACHER})
    public void deleteCourse(Authentication authentication, @PathVariable UUID courseId) {
        User userContext = (User) authentication.getPrincipal();
        courseService.deleteCourse(userContext, courseId);
    }

    @PostMapping(value = "/enroll", consumes = "application/json", produces = "application/json")
    @Secured({UserRole.ROLE_STUDENT})
    public UserPreferenceResource enrollToCourse(
            Authentication authentication,
            @Valid @RequestBody CourseEnrollResource courseEnrollResource
    ) {
        User userContext = (User) authentication.getPrincipal();
        return courseService.enrollToCourse(userContext, courseEnrollResource);
    }
}
