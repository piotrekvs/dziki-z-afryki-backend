package pl.edu.agh.io.dzikizafrykibackend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import pl.edu.agh.io.dzikizafrykibackend.db.entity.DateEntity;
import pl.edu.agh.io.dzikizafrykibackend.util.WeekEnum;

import java.time.LocalTime;

@Value
@Builder
@AllArgsConstructor
@Jacksonized
public class DateResource {
    WeekEnum weekDay;
    LocalTime startTime;
    LocalTime endTime;

    public static DateResource fromEntity(DateEntity entity) {
        return DateResource.builder()
                .weekDay(entity.getWeekDay())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .build();
    }
}
