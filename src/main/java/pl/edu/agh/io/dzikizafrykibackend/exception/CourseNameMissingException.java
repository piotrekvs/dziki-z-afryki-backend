package pl.edu.agh.io.dzikizafrykibackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Course name required")
public class CourseNameMissingException extends RuntimeException {
}
