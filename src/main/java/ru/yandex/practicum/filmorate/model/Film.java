package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.minidev.json.annotate.JsonIgnore;
import ru.yandex.practicum.filmorate.controllers.After;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Film {
    private long id;
    @NotBlank(message = "название не может быть пустым и содержать пробелы")
    private String name; // название
    @After("1895-12-28")
    private LocalDate releaseDate; // дата создания
    @Size(max = 200, message = "максимальная длина описания — 200 символов")
    private String description; // описание
    @Min(value = 0, message = "продолжительность фильма должна быть положительной")
    private long duration; // длительность
    @JsonIgnore
    private Set<Long> likes = new HashSet<>();//хранение информации о лайках: «один пользователь — один лайк»

    public Film(String name, LocalDate releaseDate, String description, long duration) {
        this.name = name;
        this.releaseDate = releaseDate;
        this.description = description;
        this.duration = duration;
    }
}
