package com.yuki920.bedwarsstats;

import java.util.ArrayList;
import java.util.List;

public class PrestigeFormatter {

    private static final List<PrestigePattern> patterns = new ArrayList<>();

    static {
        patterns.add(new PrestigePattern(100, 199, "§f[***✫]§f"));
        patterns.add(new PrestigePattern(200, 299, "§6[***✫]§f"));
        patterns.add(new PrestigePattern(300, 399, "§b[***✫]§f"));
        patterns.add(new PrestigePattern(400, 499, "§2[***✫]§f"));
        patterns.add(new PrestigePattern(500, 599, "§3[***✫]§f"));
        patterns.add(new PrestigePattern(600, 699, "§4[***✫]§f"));
        patterns.add(new PrestigePattern(700, 799, "§d[***✫]§f"));
        patterns.add(new PrestigePattern(800, 899, "§9[***✫]§f"));
        patterns.add(new PrestigePattern(900, 999, "§5[***✫]§f"));
        patterns.add(new PrestigePattern(1000, 1099, "§c[§6*§e*§a*§b*§d✫§5]§f"));
        patterns.add(new PrestigePattern(1100, 1199, "§7[§f****§7✪]§f"));
        patterns.add(new PrestigePattern(1200, 1299, "§7[§e****§6✪§7]§f"));
        patterns.add(new PrestigePattern(1300, 1399, "§7[§b****§3✪§7]§f"));
        patterns.add(new PrestigePattern(1400, 1499, "§7[§a****§2✪§7]§f"));
        patterns.add(new PrestigePattern(1500, 1599, "§7[§3****§9✪§7]§f"));
        patterns.add(new PrestigePattern(1600, 1699, "§7[§c****§4✪§7]§f"));
        patterns.add(new PrestigePattern(1700, 1799, "§7[§d****§5✪§7]§f"));
        patterns.add(new PrestigePattern(1800, 1899, "§7[§9****§1✪§7]§f"));
        patterns.add(new PrestigePattern(1900, 1999, "§7[§5****§8✪§7]§f"));
        patterns.add(new PrestigePattern(2000, 2099, "§8[§7*§f**§7*✪§8]§f"));
        patterns.add(new PrestigePattern(2100, 2199, "§f[*§e**§6*⚝]§f"));
        patterns.add(new PrestigePattern(2200, 2299, "§6[*§f**§3*⚝]§f"));
        patterns.add(new PrestigePattern(2300, 2399, "§5[*§d**§6*§e⚝]§f"));
        patterns.add(new PrestigePattern(2400, 2499, "§b[*§f**§7*⚝§8]§f"));
        patterns.add(new PrestigePattern(2500, 2599, "§f[*§a**§2*⚝]§f"));
        patterns.add(new PrestigePattern(2600, 2699, "§4[*§c**§d*⚝§5]§f"));
        patterns.add(new PrestigePattern(2700, 2799, "§e[*§f**§8*⚝]§f"));
        patterns.add(new PrestigePattern(2800, 2899, "§a[*§2**§6*⚝§e]§f"));
        patterns.add(new PrestigePattern(2900, 2999, "§b[*§3**§9*⚝§1]§f"));
        patterns.add(new PrestigePattern(3000, 3099, "§e[*§6**§c*⚝§4]§f"));
        patterns.add(new PrestigePattern(3100, 3199, "§9[*§3**§6*✥§e]§f"));
        patterns.add(new PrestigePattern(3200, 3299, "§c[§4*§7**§4*§c✥]§f"));
        patterns.add(new PrestigePattern(3300, 3399, "§9[**§d*§c*✥§4]§f"));
        patterns.add(new PrestigePattern(3400, 3499, "§2[§a*§d**§5*✥§2]§f"));
        patterns.add(new PrestigePattern(3500, 3599, "§c[*§4**§2*§a✥]§f"));
        patterns.add(new PrestigePattern(3600, 3699, "§a[**§b*§9*✥§1]§f"));
        patterns.add(new PrestigePattern(3700, 3799, "§4[*§c**§b*§3✥]§f"));
        patterns.add(new PrestigePattern(3800, 3899, "§1[*§9*§5**§d✥§1]§f"));
        patterns.add(new PrestigePattern(3900, 3999, "§c[*§a**§3*§9✥]§f"));
        patterns.add(new PrestigePattern(4000, 4099, "§5[*§c**§6*✥§e]§f"));
        patterns.add(new PrestigePattern(4100, 4199, "§e[*§6*§c*§d*✥§5]§f"));
        patterns.add(new PrestigePattern(4200, 4299, "§1[§9*§3*§b*§f*§7✥]§f"));
        patterns.add(new PrestigePattern(4300, 4399, "§0[§5*§8**§5*✥§0]§f"));
        patterns.add(new PrestigePattern(4400, 4499, "§2[*§a*§e*§6*§5✥§d]§f"));
        patterns.add(new PrestigePattern(4500, 4599, "§f[*§b**§3*✥]§f"));
        patterns.add(new PrestigePattern(4600, 4699, "§3[§b*§e**§6*§d✥§5]§f"));
        patterns.add(new PrestigePattern(4700, 4799, "§f[§4*§c**§9*§1✥§9]§f"));
        patterns.add(new PrestigePattern(4800, 4899, "§5[*§c*§6*§e*§b✥§3]§f"));
        patterns.add(new PrestigePattern(4900, 4999, "§2[§a*§f**§a*✥§2]§f"));
        patterns.add(new PrestigePattern(5000, 9999, "§4[*§5*§9**§1✥§0]§f"));
    }

    public static String formatPrestige(int stars) {
        // § はJavaでは \u00A7 と書くので、色コードを変換
        if (stars < 100) return "§7[" + stars + "✫]§f".replace('§', '\u00A7');
        
        for (PrestigePattern p : patterns) {
            if (stars >= p.min && stars <= p.max) {
                String format = p.format.replace('§', '\u00A7');
                char[] digits = String.valueOf(stars).toCharArray();
                StringBuilder result = new StringBuilder();
                int digitIndex = 0;
                
                boolean insideColorCode = false;
                for (char c : format.toCharArray()) {
                    if (c == '\u00A7') {
                        insideColorCode = true;
                        result.append(c);
                    } else if (insideColorCode) {
                        insideColorCode = false;
                        result.append(c);
                    } else if (c == '*') {
                        if (digitIndex < digits.length) {
                            result.append(digits[digitIndex++]);
                        }
                    } else {
                        result.append(c);
                    }
                }
                return result.toString();
            }
        }
        return "§7[" + stars + "✫]§f".replace('§', '\u00A7');
    }
    
    private static class PrestigePattern {
        int min, max;
        String format;

        PrestigePattern(int min, int max, String format) {
            this.min = min;
            this.max = max;
            this.format = format;
        }
    }
}
