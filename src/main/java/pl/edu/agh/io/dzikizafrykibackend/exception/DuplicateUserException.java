package pl.edu.agh.io.dzikizafrykibackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "User with this email already exists")
public class DuplicateUserException extends RuntimeException {
}
