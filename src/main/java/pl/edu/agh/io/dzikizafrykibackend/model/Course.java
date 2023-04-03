package pl.edu.agh.io.dzikizafrykibackend.model;

import lombok.Builder;
import lombok.Value;
import pl.edu.agh.io.dzikizafrykibackend.db.entity.CourseEntity;

import java.util.List;
import java.util.Set;

@Value
@Builder
public class Course {
    String name;
    String description;
    List<String> users;

//    public static Course fromEntity(CourseEntity entity) {
//        return Course.builder()
//                .name(entity.getName())
//                .users(entity.getStudents().stream()
//                               .map(StudentEntity::getStudentName)
//                               .toList())
//                .build();
//    }

    public static Course fromEntity(CourseEntity entity) {
        return Course.builder()
                .name(entity.getName())
                .users(entity.getUsers())
                .build();
    }
}
