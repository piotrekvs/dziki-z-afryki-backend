package pl.edu.agh.io.dzikizafrykibackend.model;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class CourseList {
    List<Course> courseList;
}
