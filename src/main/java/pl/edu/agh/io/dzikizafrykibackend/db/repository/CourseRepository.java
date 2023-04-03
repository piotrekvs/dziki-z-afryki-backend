package pl.edu.agh.io.dzikizafrykibackend.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.edu.agh.io.dzikizafrykibackend.db.entity.CourseEntity;

@Repository
public interface CourseRepository extends JpaRepository<CourseEntity, Integer> {
}
