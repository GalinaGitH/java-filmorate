package ru.yandex.practicum.filmorate.model;

import java.util.*;

public class SlopeOne {
    private Map<Long, Map<Long, Double>> diffInt = new HashMap<>();
    private Map<Long, Map<Long, Integer>> freqInt = new HashMap<>();
    private final Map<Long, HashMap<Long, Double>> inputDataInt;
    private final Long inputIdUser;

    public SlopeOne(Map<Long, HashMap<Long, Double>> inputDataInt, Long inputIdUser) {
        this.inputDataInt = inputDataInt;
        this.inputIdUser = inputIdUser;
    }



    public List<Long> getRecommendedIdsItemForUser(long idUser) {
        buildDifferencesMatrixInt(inputDataInt);
        HashMap<Long, Double> recomendationFilmId = predictInt(inputIdUser);

        return new ArrayList<Long>(recomendationFilmId.keySet());
    }

    private void buildDifferencesMatrixInt(Map<Long, HashMap<Long, Double>> data) {
        for (HashMap<Long, Double> user : data.values()) {
            for (Map.Entry<Long, Double> e : user.entrySet()) {
                if (!diffInt.containsKey(e.getKey())) {
                    diffInt.put(e.getKey(), new HashMap<Long, Double>());
                    freqInt.put(e.getKey(), new HashMap<Long, Integer>());
                }
                for (Map.Entry<Long, Double> e2 : user.entrySet()) {
                    int oldCount = 0;
                    if (freqInt.get(e.getKey()).containsKey(e2.getKey())) {
                        oldCount = freqInt.get(e.getKey()).get(e2.getKey());
                    }
                    double oldDiff = 0.0;
                    if (diffInt.get(e.getKey()).containsKey(e2.getKey())) {
                        oldDiff = diffInt.get(e.getKey()).get(e2.getKey());
                    }
                    double observedDiff = e.getValue() - e2.getValue();
                    freqInt.get(e.getKey()).put(e2.getKey(), oldCount + 1);
                    diffInt.get(e.getKey()).put(e2.getKey(), oldDiff + observedDiff);
                }
            }
        }
        for (Long iItem : diffInt.keySet()) {
            for (Long jItem : diffInt.get(iItem).keySet()) {
                double oldValue = diffInt.get(iItem).get(jItem);
                int count = freqInt.get(iItem).get(jItem);
                diffInt.get(iItem).put(jItem, oldValue / count);
            }
        }
    }

    private HashMap<Long, Double> predictInt(Long user) {
        HashMap<Long, Double> userRating = inputDataInt.get(user);
        Map.Entry<Long, HashMap<Long, Double>> entryUser = null;

        Optional<Map.Entry<Long, HashMap<Long, Double>>> gotEntry = inputDataInt.entrySet().stream().
                filter((en) -> en.getKey().equals(user) && en.getValue().equals(userRating)).findFirst();

        if (gotEntry.isEmpty()) {
            return null;
        }
        entryUser = gotEntry.get();
        return predictInt(entryUser);
    }

    private HashMap<Long, Double> predictInt(Map.Entry<Long, HashMap<Long, Double>> entryUserRating) {
        HashMap<Long, Double> uPred = new HashMap<>();
        HashMap<Long, Integer> uFreq = new HashMap<>();
        for (Long item : diffInt.keySet()) {
            uFreq.put(item, 0);
            uPred.put(item, 0.0);
        }
        for (Long j : entryUserRating.getValue().keySet()) {
            for (Long fromDiff : diffInt.keySet()) {
                try {
                    double predictedValue = diffInt.get(fromDiff).get(j) + entryUserRating.getValue().get(j);
                    double finalValue = predictedValue * freqInt.get(fromDiff).get(j);
                    uPred.put(fromDiff, uPred.get(fromDiff) + finalValue);
                    uFreq.put(fromDiff, uFreq.get(fromDiff) + freqInt.get(fromDiff).get(j));
                } catch (NullPointerException e1) {
                }
            }
        }

        List<Long> allUsersItems = getAllUsersItems(inputDataInt, entryUserRating.getKey());
        HashMap<Long, Double> clean = new HashMap<>();
        for (Long j : uPred.keySet()) {
            if ((uFreq.get(j) > 0) && (!(allUsersItems.contains(j)))) {
                clean.put(j, uPred.get(j) / uFreq.get(j));
            }
        }

        List<Long> allItems= getAllItemsExceptInputUsers(inputDataInt);


        for (Long j : allItems) {
            if (((entryUserRating.getValue().containsKey(j))) && !(allUsersItems.contains(j))) {
                clean.put(j, entryUserRating.getValue().get(j));
            }
        }
        return clean;
    }
    private static List<Long> getAllUsersItems(Map<Long, HashMap<Long, Double>> input, Long userId) {
        for (Long id :input.keySet()) {
            if (Objects.equals(id, userId)) {
                return new ArrayList<>(input.get(id).keySet());
            }
        }
        return null;
    }

    private static List<Long> getAllItemsExceptInputUsers(Map<Long, HashMap<Long, Double>> input) {
        HashSet<Long> itemsStore = new HashSet<>();

        for (Long userId:input.keySet()) {
            itemsStore.addAll(input.get(userId).keySet());
        }
        return new ArrayList<Long>(itemsStore);
    }

}
