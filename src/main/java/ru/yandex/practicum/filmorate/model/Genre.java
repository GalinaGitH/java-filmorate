package ru.yandex.practicum.filmorate.model;

import lombok.*;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode(of = "id")
public class Genre {
    private final int id;
    private final String name;
}