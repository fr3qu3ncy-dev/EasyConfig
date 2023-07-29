package de.fr3qu3ncy.easyconfig.spigot;

import de.fr3qu3ncy.easyconfig.core.EasyConfig;
import de.fr3qu3ncy.easyconfig.core.configuration.FileConfiguration;
import de.fr3qu3ncy.easyconfig.core.configuration.StringFormatter;
import de.fr3qu3ncy.easyconfig.core.registry.ConfigRegistry;
import de.fr3qu3ncy.easyconfig.spigot.serializable.SerializableLocation;
import de.fr3qu3ncy.easyconfig.spigot.serializable.SerializablePotion;
import de.fr3qu3ncy.easyconfig.spigot.serializable.SerializablePotionEffect;
import de.fr3qu3ncy.easyconfig.spigot.serializers.MaterialSerializer;
import lombok.SneakyThrows;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.yaml.snakeyaml.DumperOptions;

import java.io.File;
import java.lang.reflect.Field;

public class SpigotConfig extends EasyConfig {

    static {
        ConfigRegistry.register(Material.class, new MaterialSerializer());
        ConfigRegistry.register(SerializableLocation.class);
        ConfigRegistry.register(SerializablePotion.class);
        ConfigRegistry.register(SerializablePotionEffect.class);
    }

    public SpigotConfig(File configDirectory, String fileName, Class<?> holdingClass) {
        this(configDirectory, fileName, holdingClass, null);
    }

    public SpigotConfig(File configDirectory, String fileName, Class<?> holdingClass, Object holderInstance) {
        super(new SpigotFileConfig(
                loadConfig(configDirectory, fileName)),
            configDirectory, holdingClass, fileName, holderInstance);
    }

    public SpigotConfig(File configFile, Class<?> holdingClass, Object holderInstance) {
        super(new SpigotFileConfig(YamlConfiguration.loadConfiguration(configFile)), configFile.getParentFile(),
            holdingClass, splitFileName(configFile.getName()), holderInstance);
    }

    @SneakyThrows
    private static YamlConfiguration loadConfig(File configDirectory, String fileName) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(configDirectory, fileName + ".yml"));

        Field dumperOptionsField = YamlConfiguration.class.getDeclaredField("yamlDumperOptions");
        dumperOptionsField.setAccessible(true);
        DumperOptions dumperOptions = (DumperOptions) dumperOptionsField.get(config);
        dumperOptions.setIndicatorIndent(2);
        dumperOptions.setIndentWithIndicator(true);

        return config;
    }

    private static String splitFileName(String name) {
        return name.replace(".yml", "");
    }

    @Override
    protected FileConfiguration reloadInternal(File configDirectory, String fileName) {
        return new SpigotFileConfig(
            YamlConfiguration.loadConfiguration(new File(configDirectory, fileName + ".yml")));
    }

    @Override
    public StringFormatter getStringFormatter() {
        return new SpigotStringFormatter();
    }
}
