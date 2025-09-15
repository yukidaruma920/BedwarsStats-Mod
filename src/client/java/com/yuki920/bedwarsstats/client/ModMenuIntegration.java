package com.yuki920.bedwarsstats.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        // AutoConfigを使って、私たちの設定クラスから設定画面を生成して返す
        return parent -> AutoConfig.getConfigScreen(BedwarsStatsConfig.class, parent).get();
    }
}