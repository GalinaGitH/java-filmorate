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
    @NotBlank
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String name;
    private int id;
}
