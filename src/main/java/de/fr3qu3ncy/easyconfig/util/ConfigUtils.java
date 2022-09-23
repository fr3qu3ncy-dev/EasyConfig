package de.fr3qu3ncy.easyconfig.util;

import de.fr3qu3ncy.easyconfig.ConfigLocation;
import de.fr3qu3ncy.easyconfig.EasyConfig;
import org.bukkit.configuration.ConfigurationSection;

import javax.annotation.Nonnull;

public class ConfigUtils {

    private ConfigUtils() {}

    public static ConfigLocation createLocation(@Nonnull EasyConfig config, @Nonnull String path) {

        //Check if path contains a dot, if not the config itself is the parent
        if (!path.contains(".")) {
            return new ConfigLocation(config, config.getBukkitConfig(), path);
        }

        //Contains a dot

        //Split the name from path
        ConfigurationSection parentSection = splitSection(config, path);
        String name = splitName(path);

        return new ConfigLocation(config, parentSection, name);
    }

    @Nonnull
    private static ConfigurationSection splitSection(@Nonnull EasyConfig config, @Nonnull String path) {
        String parentPath = path.substring(0, path.lastIndexOf("."));
        ConfigurationSection section = config.getBukkitConfig().getConfigurationSection(parentPath);

        return section != null ? section : config.getBukkitConfig().createSection(parentPath);
    }

    @Nonnull
    private static String splitName(@Nonnull String path) {
        return path.substring(path.lastIndexOf(".") + 1);
    }
}
