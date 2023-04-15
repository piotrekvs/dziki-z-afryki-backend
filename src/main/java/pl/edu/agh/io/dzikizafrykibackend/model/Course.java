package pl.edu.agh.io.dzikizafrykibackend.model;

import lombok.Builder;
import lombok.Value;
import pl.edu.agh.io.dzikizafrykibackend.db.entity.CourseEntity;

import java.util.Set;
import java.util.stream.Collectors;

@Value
@Builder
public class Course {
    int courseId;
    String name;
    String description;
    Set<String> users;
    Set<DateResource> dates;


    public static Course fromEntity(CourseEntity entity) {
        return Course.builder()
                .courseId(entity.getId())
                .name(entity.getName())
                .description(entity.getDesc())
                .users(entity.getUsers().stream()
                               .map(e -> e.getFirstname() + " " + e.getLastname())
                               .collect(Collectors.toSet()))
                .dates(entity.getDates().stream()
                               .map(DateResource::fromEntity)
                               .collect(Collectors.toSet()))
                .build();
    }
}
