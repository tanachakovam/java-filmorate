package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;

@Data
@Builder
public class User {
    int id;
    @Email
    String email;
    @NonNull @NotBlank
    String login;
    String name;
    @PastOrPresent
    LocalDate birthday;
}
