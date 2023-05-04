package pl.edu.agh.io.dzikizafrykibackend.model;

import lombok.Builder;
import lombok.Value;
import pl.edu.agh.io.dzikizafrykibackend.db.entity.CourseEntity;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Value
@Builder
public class Course {
    UUID courseId;
    String name;
    String description;
    Set<String> users;
    Set<DateResource> dates;
    String ownerEmail;
    String courseCode;


    public static Course fromEntity(CourseEntity entity) {
        return Course.builder()
                .courseId(entity.getId())
                .name(entity.getName())
                .description(entity.getDesc())
                .users(entity.getUsers().stream()
                               .map(e -> e.getFirstName() + " " + e.getLastName())
                               .collect(Collectors.toSet()))
                .dates(entity.getDates().stream()
                               .map(DateResource::fromEntity)
                               .collect(Collectors.toSet()))
                .ownerEmail(entity.getOwnerEmail())
                .courseCode(entity.getCourseCode())
                .build();
    }
}
