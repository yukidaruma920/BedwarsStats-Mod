package com.yuki920.bedwarsstats.cache;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerStarCache {
    private static final Map<UUID, Integer> starCache = new ConcurrentHashMap<>();

    public static void addPlayer(UUID uuid, int stars) {
        starCache.put(uuid, stars);
    }

    public static Integer getStars(UUID uuid) {
        return starCache.get(uuid);
    }

    public static boolean hasPlayer(UUID uuid) {
        return starCache.containsKey(uuid);
    }
}
