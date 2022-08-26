package ru.yandex.practicum.filmorate.service.recommendation;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class SimpleSlopeOne implements RecommendationService {

    private final static double epsCompareDouble = 0.0000001;
    private final Map<Long, Integer> itemIdToIndex = new HashMap<>();
    private final Map<Integer, Long> indexToItemId = new HashMap<>();
    private Map<Long, HashMap<Long, Double>> usersItemsMap;
    private double[][] diffTable;
    private int[][] weightsTable;

    public SimpleSlopeOne() {
    }

    @Override
    public void setUsersItemsMap(Map<Long, HashMap<Long, Double>> usersItemsMap) {
        this.usersItemsMap = usersItemsMap;
    }

    @Override
    public List<Long> getRecommendedIdsItemForUser(long idUser) {
        normalizeDataBy10();
        mapItemIdsAndIndexes();
        buildDiffsAndWeights();
        Map<Long, Double> itemsRatingForUser = predictUserRating(idUser);

        return itemsRatingForUser
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue() >= 0.6)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private void normalizeDataBy10() {
        for (Map.Entry<Long, HashMap<Long, Double>> entry : usersItemsMap.entrySet()) {
            Long idUser = entry.getKey();
            for (Map.Entry<Long, Double> ratingByUser : entry.getValue().entrySet()) {
                Long idItem = ratingByUser.getKey();
                double prevValue = usersItemsMap.get(idUser).get(idItem);
                usersItemsMap.get(idUser).put(idItem, prevValue / 10);
            }
        }
    }


    private void mapItemIdsAndIndexes() {
        Set<Long> itemIds = new HashSet<>();
        for (HashMap<Long, Double> itemRatingPairs : usersItemsMap.values()) {
            Set<Long> itemIdsForIteration = itemRatingPairs.keySet();
            itemIds.addAll(itemIdsForIteration);
        }
        int index = 0;
        for (Long id : itemIds) {
            itemIdToIndex.put(id, index);
            indexToItemId.put(index, id);
            index++;
        }
    }

    private void buildDiffsAndWeights() {
        int size = itemIdToIndex.size();
        diffTable = new double[size][size];
        weightsTable = new int[size][size];
        for (HashMap<Long, Double> itemRatingPairs : usersItemsMap.values()) {
            for (Map.Entry<Long, Double> itemRatingPair1 : itemRatingPairs.entrySet()) {
                if (Math.abs(itemRatingPair1.getValue()) < epsCompareDouble) {
                    continue;
                }
                Long itemId1 = itemRatingPair1.getKey();
                int index1 = itemIdToIndex.get(itemId1);
                double rating1 = itemRatingPair1.getValue();
                for (Map.Entry<Long, Double> itemRatingPair2 : itemRatingPairs.entrySet()) {
                    if (Math.abs(itemRatingPair2.getValue()) < epsCompareDouble) {
                        continue;
                    }
                    Long itemId2 = itemRatingPair2.getKey();
                    int index2 = itemIdToIndex.get(itemId2);
                    double rating2 = itemRatingPair2.getValue();
                    int weights = weightsTable[index1][index2];
                    double diff = diffTable[index1][index2];
                    double actualDiff = rating1 - rating2;
                    weightsTable[index1][index2] = weights + 1;
                    diffTable[index1][index2] = actualDiff + diff;
                }
            }
        }
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                double numerator = diffTable[i][j];
                int denumirator = weightsTable[i][j];
                if ((Math.abs(denumirator) < epsCompareDouble) || (numerator == 0)) {
                    continue;
                }
                diffTable[i][j] = numerator / denumirator;
            }
        }
    }

    private Map<Long, Double> predictUserRating(long idUser) {
        int totalItemsRatedByUsers = diffTable.length;
        double[] predictDiff = new double[totalItemsRatedByUsers];
        int[] predictWeights = new int[totalItemsRatedByUsers];
        HashMap<Long, Double> itemsRatedByUser = usersItemsMap.get(idUser);
        if (itemsRatedByUser == null) {
            return new HashMap<>();
        }

        Set<Long> itemsIdRatedByAllUsers = itemIdToIndex.keySet();
        for (Long idItemByUser : itemsRatedByUser.keySet()) {
            for (Long idItemByAll : itemsIdRatedByAllUsers) {
                int indexByUser = itemIdToIndex.get(idItemByUser);
                int indexByAll = itemIdToIndex.get(idItemByAll);
                double value = diffTable[indexByAll][indexByUser] + itemsRatedByUser.get(idItemByUser);
                double finVal = value * weightsTable[indexByAll][indexByUser];
                predictDiff[indexByAll] += finVal;
                predictWeights[indexByAll] += weightsTable[indexByAll][indexByUser];
            }
        }
        HashMap<Long, Double> predictRating = new HashMap<>();
        for (Long idItemByAll : itemsIdRatedByAllUsers) {
            int indexByAll = itemIdToIndex.get(idItemByAll);
            if ((predictWeights[indexByAll] > 0) && (!(itemsRatedByUser.containsKey(idItemByAll)))) {
                double rating = predictDiff[indexByAll] / predictWeights[indexByAll];
                predictRating.put(idItemByAll, rating);
            }
        }
        return predictRating;
    }
}


















