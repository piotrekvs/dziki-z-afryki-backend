package pl.edu.agh.io.dzikizafrykibackend.db.entity;

import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.util.Set;

@Data
@Builder
@Entity
@Table(name = "students")
public class StudentEntity {
    @Id
    @GeneratedValue
    private int id;

    @Column(name = "name")
    private String studentName;

    @ManyToMany
    @JoinTable(
            name = "courses_students",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    Set<CourseEntity> studentCourses;
}
