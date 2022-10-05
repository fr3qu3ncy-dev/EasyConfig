package de.fr3qu3ncy.easyconfig.core.util;

import de.fr3qu3ncy.easyconfig.core.ConfigLocation;
import de.fr3qu3ncy.easyconfig.core.EasyConfig;
import de.fr3qu3ncy.easyconfig.core.FileConfigSection;

import javax.annotation.Nonnull;

public class ConfigUtils {

    private ConfigUtils() {}

    public static ConfigLocation createLocation(@Nonnull EasyConfig config, @Nonnull String path) {

        //Check if path contains a dot, if not the config itself is the parent
        if (!path.contains(".")) {
            return new ConfigLocation(config, config.getFileConfig(), path);
        }

        //Contains a dot

        //Split the name from path
        FileConfigSection parentSection = splitSection(config, path);
        String name = splitName(path);

        return new ConfigLocation(config, parentSection, name);
    }

    @Nonnull
    private static FileConfigSection splitSection(@Nonnull EasyConfig config, @Nonnull String path) {
        String parentPath = path.substring(0, path.lastIndexOf("."));
        FileConfigSection section = config.getFileConfig().getConfigurationSection(parentPath);

        return section != null ? section : config.getFileConfig().createSection(parentPath);
    }

    @Nonnull
    private static String splitName(@Nonnull String path) {
        return path.substring(path.lastIndexOf(".") + 1);
    }
}
