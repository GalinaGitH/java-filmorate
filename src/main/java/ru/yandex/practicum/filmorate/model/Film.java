package ru.yandex.practicum.filmorate.model;

import lombok.*;
import ru.yandex.practicum.filmorate.custom_validations.After;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(of = "id")
public class Film {

    @NotNull
    private Mpa mpa; //рейтинг фильма
    private long id;
    @NotBlank(message = "название не может быть пустым и содержать пробелы")
    private String name; // название
    @After("1895-12-28")
    private LocalDate releaseDate; // дата создания
    @Size(max = 200, message = "максимальная длина описания — 200 символов")
    private String description; // описание
    @Min(value = 0, message = "продолжительность фильма должна быть положительной")
    private long duration; // длительность
    private Set<Genre> genres; //информация о жанрах
    private Set<Director> directors; //режиссеры
    @Min(value = 0)
    private double rating;
}
