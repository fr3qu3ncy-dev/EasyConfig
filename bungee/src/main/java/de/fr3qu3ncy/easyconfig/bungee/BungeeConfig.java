package de.fr3qu3ncy.easyconfig.bungee;

import de.fr3qu3ncy.easyconfig.core.EasyConfig;
import de.fr3qu3ncy.easyconfig.core.preferences.Preferences;
import lombok.SneakyThrows;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;

public class BungeeConfig extends EasyConfig {

    public BungeeConfig(@Nonnull String absolutePath, @Nullable Class<?> holder,
                           @Nonnull String filePath, @Nonnull String fileName) {
        super(new BungeeFileConfig(loadConfig(filePath, fileName), "", ""), new Preferences(new BungeeStringFormatter()),
            absolutePath, holder, filePath, fileName);
    }

    public BungeeConfig(@Nonnull String absolutePath, @Nullable Class<?> holder,
                           @Nonnull String fileName) {
        super(new BungeeFileConfig(loadConfig(absolutePath, fileName), "", ""), new Preferences(new BungeeStringFormatter()),
            absolutePath, holder, fileName);
    }

    @SneakyThrows
    private static Configuration loadConfig(String filePath, String fileName) {
        return ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(filePath, fileName));
    }
}
