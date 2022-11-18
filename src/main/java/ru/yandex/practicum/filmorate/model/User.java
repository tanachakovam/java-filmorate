package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private int id;
    @Email
    private String email;
    @NonNull
    @NotBlank
    @Pattern(regexp = "^\\S*$")
    private String login;
    private String name;
    @PastOrPresent
    private LocalDate birthday;
    @JsonIgnore
    Set<Integer> friends = new HashSet<>();
}
