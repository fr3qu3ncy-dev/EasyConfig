package de.fr3qu3ncy.easyconfig.spigot;

import de.fr3qu3ncy.easyconfignew.core.EasyConfig;
import de.fr3qu3ncy.easyconfignew.core.configuration.StringFormatter;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class SpigotConfig extends EasyConfig {

    public SpigotConfig(File configDirectory, String fileName, Class<?> holdingClass) {
        super(new SpigotFileConfig(
            YamlConfiguration.loadConfiguration(new File(configDirectory, fileName + ".yml"))),
            configDirectory, holdingClass, fileName);
    }

    @Override
    public StringFormatter getStringFormatter() {
        return new SpigotStringFormatter();
    }
}
