package pl.edu.agh.io.dzikizafrykibackend.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.edu.agh.io.dzikizafrykibackend.db.entity.DateEntity;

@Repository
public interface DateRepository extends JpaRepository<DateEntity, Integer> {
}
