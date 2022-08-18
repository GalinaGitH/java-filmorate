package ru.yandex.practicum.filmorate.model;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class Feed {
    private long timestamp;
    private long userId;
    private Event eventType;
    private Operation operation;
    private long eventId;
    private long entityId;
}
