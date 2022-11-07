package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
@Builder
public class Film {
    int id;
   @NonNull @NotBlank
   String name;
    @Size(max = 200)
    String description;
    LocalDate releaseDate;
    @Positive
    int duration;
}