package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Operation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class FeedDbStorage implements ru.yandex.practicum.filmorate.storage.FeedStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addFeed(long userId, Event event, Operation operation, long entityId) {
        String sqlQuery = "insert into FEEDS (USER_ID, EVENT_TYPE, OPERATION, ENTITY_ID) values ( ?,?,?,? )";
        jdbcTemplate.update(sqlQuery, userId, event.toString(), operation.toString(), entityId);

    }

    @Override
    public List<Feed> getFeeds(int userId) {
        String sqlQuery = "select * from FEEDS where USER_ID = ?";
        return jdbcTemplate.query(sqlQuery, this::makeFeed, userId);
    }

    private Feed makeFeed(ResultSet rs, int rowNum) throws SQLException {
        return Feed.builder()
                .timestamp(rs.getTimestamp("TIMESTAMP_FEED").toInstant().toEpochMilli())
                .userId(rs.getInt("USER_ID"))
                .eventType(Event.valueOf(rs.getString("EVENT_TYPE")))
                .operation(Operation.valueOf(rs.getString("OPERATION")))
                .eventId(rs.getInt("EVENT_ID"))
                .entityId(rs.getInt("ENTITY_ID"))
                .build();
    }
}
