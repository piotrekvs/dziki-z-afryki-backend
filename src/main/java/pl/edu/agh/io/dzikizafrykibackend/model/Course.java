package pl.edu.agh.io.dzikizafrykibackend.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;
import pl.edu.agh.io.dzikizafrykibackend.db.entity.CourseEntity;

import java.util.List;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Course {
    int courseId;
    String name;
    String description;
    List<String> users;

    public static Course fromEntity(CourseEntity entity) {
        return Course.builder()
                .courseId(entity.getId())
                .name(entity.getName())
                .description(entity.getDesc())
                .users(entity.getUsers())
                .build();
    }
}
