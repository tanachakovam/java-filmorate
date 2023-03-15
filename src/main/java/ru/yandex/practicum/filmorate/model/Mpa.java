package ru.yandex.practicum.filmorate.model;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mpa {
    int id;
    private String name;

    public Mpa(int id) {
        this.id = id;
    }
}
