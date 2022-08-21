package ru.yandex.practicum.filmorate.model;

import lombok.*;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@ToString
public class Like {
    private final Long user_id;
    private final Long film_id;
}
