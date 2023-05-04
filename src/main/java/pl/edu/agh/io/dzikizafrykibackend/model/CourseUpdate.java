package pl.edu.agh.io.dzikizafrykibackend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.Optional;
import java.util.Set;

@Value
@Builder
@AllArgsConstructor
@Jacksonized
public class CourseUpdate {
    @Builder.Default
    Optional<String> name = Optional.empty();
    @Builder.Default
    Optional<String> description = Optional.empty();
    @Builder.Default
    Optional<Set<String>> users = Optional.empty();
    @Builder.Default
    Optional<Set<DateResource>> dates = Optional.empty();
    @Builder.Default
    Optional<String> ownerEmail = Optional.empty();
}
