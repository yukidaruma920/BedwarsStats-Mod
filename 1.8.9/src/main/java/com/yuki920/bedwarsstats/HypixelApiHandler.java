package com.yuki920.bedwarsstats;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
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
                String apiKey = BedwarsStatsConfig.apiKey;
                if (apiKey == null || apiKey.isEmpty()) return;

                String response = sendHttpRequest(HYPIXEL_KEY_API_URL, apiKey);
                if (response == null) return;

                JsonObject json = GSON.fromJson(response, JsonObject.class);
                if (json != null && !json.get("success").getAsBoolean()) {
                    sendMessageToPlayer(EnumChatFormatting.RED + "[BedwarsStats] Your Hypixel API key appears to be invalid or expired!");
                    sendMessageToPlayer(EnumChatFormatting.YELLOW + "Run " + EnumChatFormatting.GREEN + "/bwm settings apikey <key> " + EnumChatFormatting.YELLOW + "to set a new one.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void processPlayer(String username) {
        processPlayer(username, BedwarsStatsConfig.bedwarsMode);
    }

    public static void processPlayer(String username, BedwarsStatsConfig.BedwarsMode mode) {
        CompletableFuture.runAsync(() -> {
            try {
                String myNick = BedwarsStatsConfig.myNick;
                if (myNick != null && !myNick.isEmpty() && myNick.equalsIgnoreCase(username)) {
                    Minecraft mc = Minecraft.getMinecraft();
                    if (mc.thePlayer != null) {
                        String uuid = mc.thePlayer.getUniqueID().toString().replace("-", "");
                        fetchAndDisplayStats(uuid, username, mode);
                    }
                } else {
                    String mojangUrl = "https://api.mojang.com/users/profiles/minecraft/" + username;
                    String mojangResponse = sendHttpRequest(mojangUrl, null);
                    if (mojangResponse == null) {
                        sendMessageToPlayer(EnumChatFormatting.YELLOW + username + EnumChatFormatting.RESET + " is nicked, stats cannot be retrieved.");
                        return;
                    }
                    JsonObject mojangJson = GSON.fromJson(mojangResponse, JsonObject.class);
                    if (mojangJson == null || !mojangJson.has("id")) {
                        sendMessageToPlayer(EnumChatFormatting.YELLOW + username + EnumChatFormatting.RESET + " is nicked, stats cannot be retrieved.");
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
        String apiKey = BedwarsStatsConfig.apiKey;
        if (apiKey == null || apiKey.isEmpty()) {
            sendMessageToPlayer(EnumChatFormatting.RED + "Hypixel API Key not set!");
            return;
        }
        String hypixelUrl = HYPIXEL_API_URL + uuid;
        String hypixelResponse = sendHttpRequest(hypixelUrl, apiKey);
        if (hypixelResponse == null) return;
        JsonObject hypixelJson = GSON.fromJson(hypixelResponse, JsonObject.class);

        if (hypixelJson != null && !hypixelJson.get("success").getAsBoolean()) {
            if (hypixelJson.has("cause") && hypixelJson.get("cause").getAsString().equals("Invalid API key")) {
                sendMessageToPlayer(EnumChatFormatting.RED + "Your Hypixel API key is invalid!");
                return;
            }
        }

        if (hypixelJson == null || !hypixelJson.has("player") || hypixelJson.get("player").isJsonNull()) {
            sendMessageToPlayer(EnumChatFormatting.YELLOW + displayUsername + EnumChatFormatting.RESET + " is nicked, stats cannot be retrieved.");
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

    private static String formatStats(JsonObject player, BedwarsStatsConfig.BedwarsMode mode) {
        String username = player.get("displayname").getAsString();
        String rankPrefix = getRankPrefix(player);

        if (!player.has("stats") || player.get("stats").isJsonNull() || !player.getAsJsonObject("stats").has("Bedwars")) {
            return rankPrefix + username + EnumChatFormatting.GRAY + ": No Bedwars stats found.";
        }

        JsonObject bedwars = player.getAsJsonObject("stats").getAsJsonObject("Bedwars");
        String prefix = mode.getApiPrefix();

        int stars = (player.has("achievements") && player.getAsJsonObject("achievements").has("bedwars_level"))
                ? player.getAsJsonObject("achievements").get("bedwars_level").getAsInt() : 0;
        int wins = getInt(bedwars, prefix + "wins_bedwars");
        int losses = getInt(bedwars, prefix + "losses_bedwars");
        int finalKills = getInt(bedwars, prefix + "final_kills_bedwars");
        int finalDeaths = getInt(bedwars, prefix + "final_deaths_bedwars");

        double wlr = (losses == 0) ? wins : (double) wins / losses;
        double fkdr = (finalDeaths == 0) ? finalKills : (double) finalKills / finalDeaths;

        String prestige = PrestigeFormatter.formatPrestige(stars);
        String winsColor = getWinsColor(wins);
        String wlrColor = getWlrColor(wlr);
        String finalsColor = getFinalsColor(finalKills);
        String fkdrColor = getFkdrColor(fkdr);

        return String.format("%s %s%s%s: Wins %s%s%s | WLR %s%.2f%s | Finals %s%s%s | FKDR %s%.2f",
                prestige, rankPrefix, username, EnumChatFormatting.RESET,
                winsColor, String.format("%,d", wins), EnumChatFormatting.RESET,
                wlrColor, wlr, EnumChatFormatting.RESET,
                finalsColor, String.format("%,d", finalKills), EnumChatFormatting.RESET,
                fkdrColor, fkdr);
    }

    private static String getRankPrefix(JsonObject player) {
        String rank = player.has("rank") && !player.get("rank").getAsString().equals("NORMAL") ? player.get("rank").getAsString() : null;
        String monthlyPackageRank = player.has("monthlyPackageRank") && !player.get("monthlyPackageRank").getAsString().equals("NONE") ? player.get("monthlyPackageRank").getAsString() : null;
        String newPackageRank = player.has("newPackageRank") && !player.get("newPackageRank").getAsString().equals("NONE") ? player.get("newPackageRank").getAsString() : null;

        if (rank != null && rank.equals("YOUTUBER")) return EnumChatFormatting.RED + "[" + EnumChatFormatting.WHITE + "YOUTUBE" + EnumChatFormatting.RED + "] ";

        String displayRank = monthlyPackageRank != null && !monthlyPackageRank.equals("NONE") ? monthlyPackageRank : newPackageRank;
        if (displayRank == null) return EnumChatFormatting.GRAY.toString();

        switch (displayRank) {
            case "VIP": return EnumChatFormatting.GREEN + "[VIP] ";
            case "VIP_PLUS": return EnumChatFormatting.GREEN + "[VIP" + EnumChatFormatting.GOLD + "+" + EnumChatFormatting.GREEN + "] ";
            case "MVP": return EnumChatFormatting.AQUA + "[MVP] ";
            case "MVP_PLUS":
                String plusColor = player.has("rankPlusColor") ? "§" + player.get("rankPlusColor").getAsString().toLowerCase().charAt(0) : EnumChatFormatting.RED.toString();
                return EnumChatFormatting.AQUA + "[MVP" + plusColor + "+" + EnumChatFormatting.AQUA + "] ";
            case "SUPERSTAR":
                 String plusColorSuperstar = player.has("rankPlusColor") ? "§" + player.get("rankPlusColor").getAsString().toLowerCase().charAt(0) : EnumChatFormatting.RED.toString();
                return EnumChatFormatting.GOLD + "[MVP" + plusColorSuperstar + "++" + EnumChatFormatting.GOLD + "] ";
            default: return EnumChatFormatting.GRAY.toString();
        }
    }

    private static String getFkdrColor(double fkdr) {
        if (fkdr >= 10) return EnumChatFormatting.DARK_RED.toString();
        if (fkdr >= 5) return EnumChatFormatting.RED.toString();
        if (fkdr >= 3) return EnumChatFormatting.GOLD.toString();
        if (fkdr >= 2) return EnumChatFormatting.YELLOW.toString();
        if (fkdr >= 1) return EnumChatFormatting.GREEN.toString();
        return EnumChatFormatting.GRAY.toString();
    }

    private static String getWlrColor(double wlr) {
        if (wlr >= 5) return EnumChatFormatting.DARK_RED.toString();
        if (wlr >= 3) return EnumChatFormatting.RED.toString();
        if (wlr >= 2) return EnumChatFormatting.GOLD.toString();
        if (wlr >= 1) return EnumChatFormatting.YELLOW.toString();
        if (wlr >= 0.5) return EnumChatFormatting.GREEN.toString();
        return EnumChatFormatting.GRAY.toString();
    }

    private static String getWinsColor(int wins) {
        if (wins >= 10000) return EnumChatFormatting.DARK_RED.toString();
        if (wins >= 5000) return EnumChatFormatting.RED.toString();
        if (wins >= 1000) return EnumChatFormatting.GOLD.toString();
        if (wins >= 500) return EnumChatFormatting.YELLOW.toString();
        if (wins >= 100) return EnumChatFormatting.GREEN.toString();
        return EnumChatFormatting.GRAY.toString();
    }

    private static String getFinalsColor(int finals) {
        if (finals >= 25000) return EnumChatFormatting.DARK_RED.toString();
        if (finals >= 10000) return EnumChatFormatting.RED.toString();
        if (finals >= 5000) return EnumChatFormatting.GOLD.toString();
        if (finals >= 2500) return EnumChatFormatting.YELLOW.toString();
        if (finals >= 1000) return EnumChatFormatting.GREEN.toString();
        return EnumChatFormatting.GRAY.toString();
    }

    private static int getInt(JsonObject obj, String memberName) {
        return obj.has(memberName) ? obj.get(memberName).getAsInt() : 0;
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
        Minecraft.getMinecraft().addScheduledTask(() -> {
            if (Minecraft.getMinecraft().thePlayer != null) {
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(message));
            }
        });
    }
}