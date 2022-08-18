package ru.yandex.practicum.filmorate.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewLike {

    long userId;
    long reviewId;
    boolean isUseful;
}
