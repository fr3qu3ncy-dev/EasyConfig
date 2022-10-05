package de.fr3qu3ncy.easyconfig.bungee;

import de.fr3qu3ncy.easyconfig.core.preferences.StringFormatter;
import net.md_5.bungee.api.ChatColor;

public class BungeeStringFormatter implements StringFormatter {
    @Override
    public String replaceColor(String color) {
        return ChatColor.of(color).toString();
    }

    @Override
    public String translateAlternateColorCodes(char c, String str) {
        return ChatColor.translateAlternateColorCodes(c, str);
    }
}
