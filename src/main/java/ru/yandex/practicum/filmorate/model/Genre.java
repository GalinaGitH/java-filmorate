package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode(of = "id")
public class Genre {
    private final int id;
    @NotBlank
    private final String name;
}