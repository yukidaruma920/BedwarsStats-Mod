package com.yuki920.bedwarsstats.hud;

import com.yuki920.bedwarsstats.stats.PlayerStats;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HudData {
    private static final HudData INSTANCE = new HudData();
    private final List<PlayerStats> playerStats = Collections.synchronizedList(new ArrayList<>());

    private HudData() {}

    public static HudData getInstance() {
        return INSTANCE;
    }

    public void clear() {
        playerStats.clear();
    }

    public void addPlayerStat(PlayerStats stat) {
        playerStats.add(stat);
    }

    public List<PlayerStats> getPlayerStats() {
        // Return a copy to prevent concurrent modification issues during rendering
        return new ArrayList<>(playerStats);
    }
}
