package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Director {
    private int id;
    @NotBlank
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String name;
}
