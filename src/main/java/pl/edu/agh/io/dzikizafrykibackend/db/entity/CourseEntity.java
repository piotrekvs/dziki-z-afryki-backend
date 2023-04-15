package pl.edu.agh.io.dzikizafrykibackend.db.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@EqualsAndHashCode(exclude = {"users", "dates"})
@ToString(exclude = {"users", "dates"})
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "courses")
public class CourseEntity {
    @Id
    @GeneratedValue
    private int id;

    @Column(name = "name")
    @NotNull
    private String name;

    @Column(name = "description")
    private String desc;

    @ManyToMany
    @JoinTable(
            name = "courses_users",
            inverseJoinColumns = @JoinColumn(name = "user_id"),
            joinColumns = @JoinColumn(name = "course_id")
    )
    Set<UserEntity> users;

    @ManyToMany
    @JoinTable(
            name = "courses_dates",
            inverseJoinColumns = @JoinColumn(name = "date_id"),
            joinColumns = @JoinColumn(name = "course_id")
    )
    Set<DateEntity> dates;
}
