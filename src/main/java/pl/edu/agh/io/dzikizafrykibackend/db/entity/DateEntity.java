package pl.edu.agh.io.dzikizafrykibackend.db.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.edu.agh.io.dzikizafrykibackend.util.WeekEnum;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "dates")
public class DateEntity {
    @Id
    @GeneratedValue
    private int id;

    @Column(name = "weekDay")
    @NotNull
    private WeekEnum weekDay;

    @Column(name = "startTime")
    @NotNull
    private LocalTime startTime;

    @Column(name = "endTime")
    @NotNull
    private LocalTime endTime;

    @ManyToMany(mappedBy = "dates")
    Set<CourseEntity> dateCourses;

    @ManyToMany(mappedBy = "userDates")
    Set<UserEntity> users;
}
