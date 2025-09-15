package com.yuki920.bedwarsstats.hud;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HudData {
    private static final HudData INSTANCE = new HudData();
    private final List<String> statsLines = Collections.synchronizedList(new ArrayList<>());

    private HudData() {}

    public static HudData getInstance() {
        return INSTANCE;
    }

    public void clear() {
        statsLines.clear();
    }

    public void addLine(String line) {
        statsLines.add(line);
    }

    public List<String> getLines() {
        // Return a copy to prevent concurrent modification issues during rendering
        return new ArrayList<>(statsLines);
    }
}
