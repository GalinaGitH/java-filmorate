package ru.yandex.practicum.filmorate.service.recommendation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface RecommendationService {
    void setUsersItemsMap(Map<Long, HashMap<Long, Double>> usersItemsMap);

    List<Long> getRecommendedIdsItemForUser(long idUser);
}
