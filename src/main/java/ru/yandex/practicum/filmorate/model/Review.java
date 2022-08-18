package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(of = "reviewId")
public class Review {

    private long reviewId;
    @NotNull
    private Long userId; //пользователь
    @NotNull
    private Long filmId; //фильм
    @NotNull
    private Boolean isPositive; // тип отзывы
    @NotEmpty
    private String content; //описание
    private Integer useful; //рейтинг полезности, расчетное поле
}
