package ru.yandex.practicum.filmorate.model;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Genre {
    private int id;
    private String name;

    public Genre(int id) {
        this.id = id;
    }
}
