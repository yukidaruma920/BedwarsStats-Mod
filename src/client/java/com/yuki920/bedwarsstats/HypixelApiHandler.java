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
    private static final String HYPIXEL_KEY_API_URL = "https://api.hypixel.net/key";

    public static void checkApiKeyValidity() {
        CompletableFuture.runAsync(() -> {
            try {
                BedwarsStatsConfig config = AutoConfig.getConfigHolder(BedwarsStatsConfig.class).getConfig();
                String apiKey = config.apiKey;

                if (apiKey == null || apiKey.isEmpty()) {
                    return; // No key set, so nothing to check.
                }

                String response = sendHttpRequest(HYPIXEL_KEY_API_URL, apiKey);
                if (response == null) return; // Request failed, but don't spam chat.

                JsonObject json = GSON.fromJson(response, JsonObject.class);
                if (json != null && !json.get("success").getAsBoolean()) {
                    sendMessageToPlayer("§c[BedwarsStats] Your Hypixel API key appears to be invalid or expired!");
                    sendMessageToPlayer("§eRun §a/bwm settings apikey <key> §eto set a new one.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void processPlayer(String username) {
        BedwarsStatsConfig config = AutoConfig.getConfigHolder(BedwarsStatsConfig.class).getConfig();
        processPlayer(username, config.bedwarsMode);
    }

    public static void processPlayer(String username, BedwarsStatsConfig.BedwarsMode mode) {
        CompletableFuture.runAsync(() -> {
            try {
                BedwarsStatsConfig config = AutoConfig.getConfigHolder(BedwarsStatsConfig.class).getConfig();
                String myNick = config.myNick;

                if (myNick != null && !myNick.isEmpty() && myNick.equalsIgnoreCase(username)) {
                    MinecraftClient client = MinecraftClient.getInstance();
                    if (client.player != null) {
                        String uuid = client.player.getUuid().toString();
                        fetchAndDisplayStats(uuid, username, mode);
                    }
                } else {
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
                    fetchAndDisplayStats(uuid, username, mode);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static void fetchAndDisplayStats(String uuid, String displayUsername, BedwarsStatsConfig.BedwarsMode mode) throws Exception {
        BedwarsStatsConfig config = AutoConfig.getConfigHolder(BedwarsStatsConfig.class).getConfig();
        String apiKey = config.apiKey;
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
            sendMessageToPlayer("§e" + displayUsername + " §ris nicked, stats cannot be retrieved.");
            return;
        }
        JsonObject player = hypixelJson.getAsJsonObject("player");

        if (!player.get("displayname").getAsString().equalsIgnoreCase(displayUsername)) {
            player.addProperty("displayname", displayUsername);
        }

        String chatMessage = formatStats(player, mode);
        if (chatMessage != null) {
            sendMessageToPlayer(chatMessage);
        }
    }

    private static String formatNumber(int number) {
        return String.format("%,d", number);
    }

    private static String getFkdrColor(double fkdr) {
        if (fkdr >= 20) return "§5";
        if (fkdr >= 15) return "§d";
        if (fkdr >= 10) return "§4";
        if (fkdr >= 8)  return "§c";
        if (fkdr >= 6)  return "§6";
        if (fkdr >= 4)  return "§e";
        if (fkdr >= 2)  return "§2";
        if (fkdr >= 1)  return "§a";
        if (fkdr >= 0.5) return "§f";
        return "§7";
    }

    private static String getWlrColor(double wlr) {
        if (wlr >= 10) return "§5";
        if (wlr >= 8)  return "§d";
        if (wlr >= 6)  return "§4";
        if (wlr >= 5)  return "§c";
        if (wlr >= 4)  return "§6";
        if (wlr >= 3)  return "§e";
        if (wlr >= 2)  return "§2";
        if (wlr >= 1)  return "§a";
        if (wlr >= 0.5) return "§f";
        return "§7";
    }

    private static String getWinsColor(int wins) {
        if (wins >= 50000) return "§5";
        if (wins >= 25000) return "§d";
        if (wins >= 10000) return "§4";
        if (wins >= 5000) return "§c";
        if (wins >= 2500) return "§6";
        if (wins >= 1000) return "§e";
        if (wins >= 500) return "§2";
        if (wins >= 250) return "§a";
        if (wins >= 50) return "§f";
        return "§7";
    }

    private static String getFinalsColor(int finals) {
        if (finals >= 100000) return "§5";
        if (finals >= 50000) return "§d";
        if (finals >= 25000) return "§4";
        if (finals >= 10000) return "§c";
        if (finals >= 5000) return "§6";
        if (finals >= 2500) return "§e";
        if (finals >= 1000) return "§2";
        if (finals >= 500) return "§a";
        if (finals >= 100) return "§f";
        return "§7";
    }

    private static String getWsColor(int ws) {
        if (ws >= 100) return "§4";
        if (ws >= 50) return "§6";
        if (ws >= 25) return "§e";
        if (ws >= 10) return "§a";
        if (ws >= 5) return "§2";
        return "§7";
    }

    private static int getInt(JsonObject obj, String memberName) {
        return obj.has(memberName) ? obj.get(memberName).getAsInt() : 0;
    }

    private static String formatStats(JsonObject player, BedwarsStatsConfig.BedwarsMode mode) {
        BedwarsStatsConfig config = AutoConfig.getConfigHolder(BedwarsStatsConfig.class).getConfig();
        String prefix = mode.getApiPrefix();
        String username = player.get("displayname").getAsString();
        String rankPrefix = config.showRankPrefix ? getRankPrefix(player) : "";

        if (!player.has("stats") || player.get("stats").isJsonNull() || !player.getAsJsonObject("stats").has("Bedwars")) {
            return rankPrefix + username + "§7: No Bedwars stats found.";
        }

        JsonObject bedwars = player.getAsJsonObject("stats").getAsJsonObject("Bedwars");
        int stars = getInt(player.getAsJsonObject("achievements"), "bedwars_level");
        int wins = getInt(bedwars, prefix + "wins_bedwars");
        int losses = getInt(bedwars, prefix + "losses_bedwars");
        int finalKills = getInt(bedwars, prefix + "final_kills_bedwars");
        int finalDeaths = getInt(bedwars, prefix + "final_deaths_bedwars");
        int winstreak = getInt(bedwars, prefix + "winstreak");

        double wlr = (losses == 0) ? wins : (double) wins / losses;
        double fkdr = (finalDeaths == 0) ? finalKills : (double) finalKills / finalDeaths;

        String prestige = PrestigeFormatter.formatPrestige(stars);
        String winsColor = getWinsColor(wins);
        String wlrColor = getWlrColor(wlr);
        String finalsColor = getFinalsColor(finalKills);
        String fkdrColor = getFkdrColor(fkdr);
        String wsColor = getWsColor(winstreak);

        return String.format("%s %s%s§r §7[%s]§r: Wins %s%s§r | WLR %s%.2f§r | Finals %s%s§r | FKDR %s%.2f§r | WS %s%d",
                prestige, rankPrefix, username, mode.getDisplayName(),
                winsColor, formatNumber(wins), wlrColor, wlr,
                finalsColor, formatNumber(finalKills), fkdrColor, fkdr,
                wsColor, winstreak);
    }

    private static String getRankPrefix(JsonObject player) {
        String rank = player.has("rank") && !player.get("rank").getAsString().equals("NORMAL") ? player.get("rank").getAsString() : null;
        String monthlyPackageRank = player.has("monthlyPackageRank") && !player.get("monthlyPackageRank").getAsString().equals("NONE") ? player.get("monthlyPackageRank").getAsString() : null;
        String newPackageRank = player.has("newPackageRank") && !player.get("newPackageRank").getAsString().equals("NONE") ? player.get("newPackageRank").getAsString() : null;
        String rankPlusColorStr = player.has("rankPlusColor") ? player.get("rankPlusColor").getAsString() : null;

        if (rank != null) {
            switch (rank) {
                case "YOUTUBER": return "§c[§fYOUTUBE§c] ";
                case "STAFF": return "§c[§6ዞ§c] ";
            }
        }

        String displayRank = monthlyPackageRank != null ? monthlyPackageRank : newPackageRank;
        if (displayRank == null) return "§7";

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