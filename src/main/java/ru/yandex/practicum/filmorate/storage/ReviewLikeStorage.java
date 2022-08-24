package ru.yandex.practicum.filmorate.storage;


import ru.yandex.practicum.filmorate.model.ReviewLike;

public interface ReviewLikeStorage {

    void create(ReviewLike reviewLike);

    void removeReviewLike(ReviewLike reviewLike);
}
