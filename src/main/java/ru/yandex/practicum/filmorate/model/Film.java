package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.yandex.practicum.filmorate.controllers.After;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class Film {
    private int id;
    @NotBlank(message = "название не может быть пустым и содержать пробелы")
    private final String name; // название
    @After("1895-12-28")
    private final LocalDate releaseDate; // дата создания
    @Size(max = 200, message = "максимальная длина описания — 200 символов")
    private String description; // описание
    @Min(value = 0, message = "продолжительность фильма должна быть положительной")
    private long duration; // длительность
}
