package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.storage.FeedStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedService {
    private final FeedStorage feedStorage;
    private final UserService userService;


    public void addLikeFilmInFeed(long filmId, long userId) {
        feedStorage.addFeed(userId, Event.LIKE, Operation.ADD, filmId);
    }

    public void removeLikeFilmInFeed(long filmId, long userId) {
        feedStorage.addFeed(userId, Event.LIKE, Operation.REMOVE, filmId);
    }

    public void addReviewFilmInFeed(long userId, long reviewId) {
        feedStorage.addFeed(userId, Event.REVIEW, Operation.ADD, reviewId);
    }

    public void updateReviewFilmInFeed(long userId, long reviewId) {
        feedStorage.addFeed(userId, Event.REVIEW, Operation.UPDATE, reviewId);
    }

    public void removeReviewFilmInFeed(long userId, long reviewId) {
        feedStorage.addFeed(userId, Event.REVIEW, Operation.REMOVE, reviewId);
    }

    public void addFriendInFeed(long userId, long friendId) {
        feedStorage.addFeed(userId, Event.FRIEND, Operation.ADD, friendId);
    }

    public void removeFriendInFeed(long userId, long friendId) {
        feedStorage.addFeed(userId, Event.FRIEND, Operation.REMOVE, friendId);
    }

    public List<Feed> getFeeds(int userId) {
        userService.get(userId);
        return feedStorage.getFeeds(userId);
    }

}
