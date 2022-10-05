package de.fr3qu3ncy.easyconfig.spigot;

import de.fr3qu3ncy.easyconfig.core.EasyConfig;
import de.fr3qu3ncy.easyconfig.core.preferences.Preferences;
import org.bukkit.configuration.file.YamlConfiguration;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;

public class SpigotConfig extends EasyConfig {

    public SpigotConfig(@Nonnull String absolutePath,
                           @Nullable Class<?> holder, @Nonnull String filePath, @Nonnull String fileName) {
        super(new SpigotFileConfig(loadConfig(filePath, fileName)), new Preferences(new SpigotStringFormatter()),
            absolutePath, holder, filePath, fileName);
    }

    public SpigotConfig(@Nonnull String absolutePath,
                           @Nullable Class<?> holder, @Nonnull String fileName) {
        super(new SpigotFileConfig(loadConfig(absolutePath, fileName)), new Preferences(new SpigotStringFormatter()),
            absolutePath, holder, fileName);
    }

    private static YamlConfiguration loadConfig(String filePath, String fileName) {
        return YamlConfiguration.loadConfiguration(new File(filePath, fileName));
    }
}
