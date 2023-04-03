package pl.edu.agh.io.dzikizafrykibackend.model;

import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Value
@Builder
public class CourseUpdate {
    Optional<String> name;
    Optional<String> description;
    Optional<List<String>> users;
}
