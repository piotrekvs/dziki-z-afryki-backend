package pl.edu.agh.io.dzikizafrykibackend.db.entity;

import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Data
@Builder
@Entity
@Table(name = "courses")
public class CourseEntity {
    @Id
    @GeneratedValue
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String desc;

    @ManyToMany(mappedBy = "studentCourses")
    Set<StudentEntity> students;

    @Column(name = "users")
    @ElementCollection
    private List<String> users;
}
