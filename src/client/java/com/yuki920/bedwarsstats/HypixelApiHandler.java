package com.yuki920.bedwarsstats;

import com.yuki920.bedwarsstats.config.BedwarsStatsConfig;
import com.google.gson.Gson;
import me.shedaniel.autoconfig.AutoConfig;
import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

public class HypixelApiHandler {
    private static final Gson GSON = new Gson();
    private static final String HYPIXEL_API_URL = "https://api.hypixel.net/player?uuid=";

    public static void processPlayer(String username) {
        CompletableFuture.runAsync(() -> {
            try {
                String mojangUrl = "https://api.mojang.com/users/profiles/minecraft/" + username;
                String mojangResponse = sendHttpRequest(mojangUrl, null);
                if (mojangResponse == null) {
                    sendMessageToPlayer("§e" + username + " §ris nicked, stats cannot be retrieved.");
                    return;
                }
                JsonObject mojangJson = GSON.fromJson(mojangResponse, JsonObject.class);
                if (mojangJson == null || !mojangJson.has("id")) {
                    sendMessageToPlayer("§e" + username + " §ris nicked, stats cannot be retrieved.");
                    return;
                }
                String uuid = mojangJson.get("id").getAsString();

                String apiKey = AutoConfig.getConfigHolder(BedwarsStatsConfig.class).getConfig().apiKey;
                if (apiKey == null || apiKey.isEmpty()) {
                    sendMessageToPlayer("§cHypixel API Key not set!");
                    return;
                }
                String hypixelUrl = HYPIXEL_API_URL + uuid;
                String hypixelResponse = sendHttpRequest(hypixelUrl, apiKey);
                if (hypixelResponse == null) return;
                JsonObject hypixelJson = GSON.fromJson(hypixelResponse, JsonObject.class);

                if (hypixelJson != null && !hypixelJson.get("success").getAsBoolean()) {
                    if (hypixelJson.has("cause") && hypixelJson.get("cause").getAsString().equals("Invalid API key")) {
                        sendMessageToPlayer("§cYour Hypixel API key is invalid!");
                        sendMessageToPlayer("§ePlease get a new one from the Hypixel Developer Dashboard:");
                        sendMessageToPlayer("§bhttps://developer.hypixel.net/");
                        return;
                    }
                }
                
                if (hypixelJson == null || !hypixelJson.has("player") || hypixelJson.get("player").isJsonNull()) {
                    sendMessageToPlayer("§e" + username + " §ris nicked, stats cannot be retrieved.");
                    return;
                }
                JsonObject player = hypixelJson.getAsJsonObject("player");

                String chatMessage = formatStats(player);
                if (chatMessage != null) {
                    sendMessageToPlayer(chatMessage);
                }
            } catch (Exception e) {
                 e.printStackTrace();
            }
        });
    }

    // ★★★ 1. 数値をフォーマットするヘルパーメソッドを追加 ★★★
    private static String formatNumber(int number) {
        return String.format("%,d", number);
    }

    // ★★★ 2. Statsごとの色付け用ヘルパーメソッド ★★★
    private static String getFkdrColor(double fkdr) {
        if (fkdr >= 10) return "§4"; // Dark Red
        if (fkdr >= 8) return "§c"; // Red
        if (fkdr >= 6) return "§6"; // Gold
        if (fkdr >= 4) return "§e"; // Yellow
        if (fkdr >= 2) return "§2";  // Dark Green
        if (fkdr >= 1) return "§a";  // Green
        return "§f"; // Green
    }
    
    private static String getWlrColor(double wlr) {
        if (wlr >= 5) return "§4"; // Dark Red
        if (wlr >= 4) return "§c"; // Red
        if (wlr >= 3) return "§6"; // Gold
        if (wlr >= 2) return "§e"; // Yellow
        if (wlr >= 1) return "§2";  // Dark Green
        if (wlr >= 0.5) return "§a";  // Green
        return "§f"; // Green
    }

    private static String getWinsColor(int wins) {
        if (wins >= 50000) return "§5"; // Dark Purple
        if (wins >= 25000) return "§d"; // Light Purple
        if (wins >= 10000) return "§4"; // Dark Red
        if (wins >= 5000) return "§c"; // Red
        if (wins >= 2500) return "§6"; // Gold
        if (wins >= 1000) return "§e"; // Yellow
        if (wins >= 500) return "§2";  // Dark Green
        if (wins >= 250) return "§a";  // Green
        if (wins >= 50) return "§f";  // White
        return "§8"; // Gray
    }

    private static String getFinalsColor(int finals) {
        if (finals >= 100000) return "§5"; // Dark Purple
        if (finals >= 50000) return "§d"; // Light Purple
        if (finals >= 25000) return "§4"; // Dark Red
        if (finals >= 10000) return "§c"; // Red
        if (finals >= 5000) return "§6"; // Gold
        if (finals >= 2500) return "§e"; // Yellow
        if (finals >= 1000) return "§2";  // Dark Green
        if (finals >= 500) return "§a";  // Green
        if (finals >= 100) return "§f";  // White
        return "§8"; // Gray
    }

    private static String formatStats(JsonObject player) {
        String username = player.get("displayname").getAsString();
        String rankPrefix = getRankPrefix(player);

        if (!player.has("stats") || player.get("stats").isJsonNull() || !player.getAsJsonObject("stats").has("Bedwars")) {
            return rankPrefix + username + "§7: No Bedwars stats found.";
        }

        JsonObject bedwars = player.getAsJsonObject("stats").getAsJsonObject("Bedwars");

        int stars = (player.has("achievements") && player.getAsJsonObject("achievements").has("bedwars_level"))
                ? player.getAsJsonObject("achievements").get("bedwars_level").getAsInt() : 0;
        int wins = bedwars.has("wins_bedwars") ? bedwars.get("wins_bedwars").getAsInt() : 0;
        int losses = bedwars.has("losses_bedwars") ? bedwars.get("losses_bedwars").getAsInt() : 0;
        int finalKills = bedwars.has("final_kills_bedwars") ? bedwars.get("final_kills_bedwars").getAsInt() : 0;
        int finalDeaths = bedwars.has("final_deaths_bedwars") ? bedwars.get("final_deaths_bedwars").getAsInt() : 0;

        double wlr = (losses == 0) ? wins : (double) wins / losses;
        double fkdr = (finalDeaths == 0) ? finalKills : (double) finalKills / finalDeaths;

        String prestige = PrestigeFormatter.formatPrestige(stars);
        String winsColor = getWinsColor(wins);
        String wlrColor = getWlrColor(wlr);
        String finalsColor = getFinalsColor(finalKills);
        String fkdrColor = getFkdrColor(fkdr);

        return String.format("%s %s%s§r: Wins %s%s§r | WLR %s%.2f§r | Finals %s%s§r | FKDR %s%.2f",
                prestige,
                rankPrefix,
                username,
                winsColor, formatNumber(wins),
                wlrColor, wlr,
                finalsColor, formatNumber(finalKills),
                fkdrColor, fkdr);
    }
    
    // ★★★ 3. getRankPrefixメソッドを最新版に更新 ★★★
    private static String getRankPrefix(JsonObject player) {
        // 関連する全てのランク情報を取得
        String rank = player.has("rank") && !player.get("rank").getAsString().equals("NORMAL") ? player.get("rank").getAsString() : null;
        String monthlyPackageRank = player.has("monthlyPackageRank") && !player.get("monthlyPackageRank").getAsString().equals("NONE") ? player.get("monthlyPackageRank").getAsString() : null;
        String newPackageRank = player.has("newPackageRank") && !player.get("newPackageRank").getAsString().equals("NONE") ? player.get("newPackageRank").getAsString() : null;
        String rankPlusColorStr = player.has("rankPlusColor") ? player.get("rankPlusColor").getAsString() : null;

        // 優先度1: スタッフランクやYoutuberランクを最優先でチェック
        if (rank != null) {
            switch (rank) {
                case "YOUTUBER":
                    return "§c[§fYOUTUBE§c] ";
                // ★★★ "ADMIN", "OWNER" などを "STAFF" に置き換え ★★★
                case "STAFF":
                    return "§c[§6ዞ§c] ";
            }
        }

        // 優先度2: 購入したランクをチェック
        String displayRank = monthlyPackageRank != null ? monthlyPackageRank : newPackageRank;

        if (displayRank == null) {
            return "§7"; // No rank
        }

        String plusColor = "§c";
        if (rankPlusColorStr != null) {
            try {
                plusColor = "§" + Formatting.valueOf(rankPlusColorStr).getCode();
            } catch (IllegalArgumentException e) {
                plusColor = "§c";

            }
        }

        switch (displayRank) {
            case "VIP": return "§a[VIP] ";
            case "VIP_PLUS": return "§a[VIP§6+§a] ";
            case "MVP": return "§b[MVP] ";
            case "MVP_PLUS": return "§b[MVP" + plusColor + "+§b] ";
            case "SUPERSTAR": return "§6[MVP" + plusColor + "++§6] ";
            default: return "§7";
        }
    }
    private static String sendHttpRequest(String urlString, String apiKey) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        if (apiKey != null) {
            connection.setRequestProperty("API-Key", apiKey);
        }
        if (connection.getResponseCode() == 200) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            return content.toString();
        }
        return null;
    }

    private static void sendMessageToPlayer(String message) {
        MinecraftClient client = MinecraftClient.getInstance();
        client.execute(() -> {
            if (client.player != null) {
                client.player.sendMessage(Text.literal(message), false);
            }
        });
    }
}
