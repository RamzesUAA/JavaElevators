package interactor.strategies;

import java.util.Map;

public class StrategyUtility {
    public static <Key, Value extends Comparable<Value>> Key getKeyOfMinimumValue(Map<Key, Value> map) {
        Key minKey = null;
        Value minValue = null;
        for (Map.Entry<Key, Value> entry : map.entrySet()) {
            if (minKey == null) {
                minKey = entry.getKey();
                minValue = entry.getValue();
            }
            if (entry.getValue().compareTo(minValue) < 0) {
                minKey = entry.getKey();
                minValue = entry.getValue();
            }
        }
        return minKey;
    }
}
