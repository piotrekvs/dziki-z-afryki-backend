package pl.edu.agh.io.dzikizafrykibackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "Only course owner can make changes to the course")
public class InvalidOwnerException extends RuntimeException {
}
