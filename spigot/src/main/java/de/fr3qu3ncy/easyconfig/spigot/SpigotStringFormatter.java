package de.fr3qu3ncy.easyconfig.spigot;

import de.fr3qu3ncy.easyconfignew.core.configuration.StringFormatter;
import net.md_5.bungee.api.ChatColor;

public class SpigotStringFormatter implements StringFormatter {
    @Override
    public String replaceColor(String color) {
        return ChatColor.of(color).toString();
    }

    @Override
    public String translateAlternateColorCodes(char c, String str) {
        return ChatColor.translateAlternateColorCodes(c, str);
    }
}
