package ru.yandex.practicum.filmorate.service.recommendation;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.SlopeOne;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SlopeOneRecommend implements RecommendationService {

    private Map<Long, HashMap<Long, Double>> usersItemsMap;

    @Override
    public void setUsersItemsMap(Map<Long, HashMap<Long, Double>> usersItemsMap) {
        this.usersItemsMap = usersItemsMap;
    }

    @Override
    public List<Long> getRecommendedIdsItemForUser(long idUser) {
        SlopeOne composite = new SlopeOne(usersItemsMap, idUser);
        return composite.getRecommendedIdsItemForUser(idUser);
    }
}

