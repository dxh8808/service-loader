package com.nd.sdp.android.serviceloader.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Young on 2018/4/26.
 */

public final class MapUtils {

    public static <K, V> void joinMapList(K key, V value, Map<K, List<V>> map) {
        if (map.containsKey(key)) {
            List<V> list = map.get(key);
            list.add(value);
            return;
        }
        List<V> list = new ArrayList<>();
        list.add(value);
        map.put(key, list);
    }

}
