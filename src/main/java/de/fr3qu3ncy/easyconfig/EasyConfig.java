package de.fr3qu3ncy.easyconfig;

import de.fr3qu3ncy.easyconfig.annotation.ConfigPath;
import de.fr3qu3ncy.easyconfig.io.ConfigWriter;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.bukkit.configuration.file.YamlConfiguration;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EasyConfig {

    @Setter
    private static boolean debug = false;

    @Nonnull
    private final String absolutePath;

    @Nullable
    @Setter
    private Class<?> holder;

    @Nonnull
    private final String filePath;

    @Nonnull
    @Getter
    private final String fileName;

    @Getter
    private File configFile;

    @Getter
    private YamlConfiguration bukkitConfig;

    @Nonnull
    private final ConfigWriter writer;

    public EasyConfig(@Nonnull String absolutePath, @Nullable Class<?> holder, @Nonnull String filePath, @Nonnull String fileName) {
        this.absolutePath = absolutePath;
        this.holder = holder;
        this.filePath = filePath;
        this.fileName = fileName;

        this.writer = new ConfigWriter(this);
    }

    public EasyConfig(@Nonnull String absolutePath, @Nullable Class<?> holder, @Nonnull String fileName) {
        this(absolutePath, holder, "", fileName);
    }

    void logInfo(String message) {
        if (!debug) return;

        System.out.println("[EasyConfig] " + message);
    }

    void logError(String message) {
        System.out.println("[EasyConfig ERROR] " + message);
    }

    public void load() {
        //Create config if not exists
        createConfig();

        //Cannot load config if there is no holder
        if (holder == null) return;

        for (Field field : holder.getFields()) {
            //Check if field is valid
            if (!checkFieldValid(field)) continue;

            logInfo("Loading field " + field.getName() + " in holder class " + holder.getName() + ".");
            loadHolderField(new HolderField(this, field));
        }
        writer.replaceComments();
    }

    private void loadHolderField(HolderField holderField) {
        //Check if field has default that is not already saved to the config
        if (holderField.getDefaultValue() != null && !bukkitConfig.contains(holderField.getPath())) {
            ConfigIO.saveInConfig(this, holderField);
            ConfigIO.writeToField(holderField);
        } else if (bukkitConfig.contains(holderField.getPath())) {
            //Path already has a value in config, now write it to the field
            ConfigIO.writeToField(holderField);
        }
    }

    private boolean checkFieldValid(Field field) {
        //Field needs to be public static and be at least annotated with ConfigPath
        return field.isAnnotationPresent(ConfigPath.class) && Modifier.isStatic(field.getModifiers());
    }

    @SneakyThrows
    private void createConfig() {
        //Only create if not already created
        if (configFile == null || bukkitConfig == null) {
            //Create the needed directory
            File configDir = getFileDirectory();
            if (!configDir.exists() && !configDir.mkdirs()) {
                throw new IOException("Couldn't create config directory!");
            }

            //Create the config file
            configFile = new File(configDir, fileName + ".yml");
            if (!configFile.exists() && !configFile.createNewFile()) {
                throw new IOException("Couldn't create config file!");
            }

            //Load the configuration from file
            this.bukkitConfig = YamlConfiguration.loadConfiguration(configFile);

            //Set line width for comments
            this.bukkitConfig.options().width(128);

            //Set copyDefaults true for automatic value adding
            bukkitConfig.options().copyDefaults(true);
        }
    }

    public File getFileDirectory() {
        return new File(absolutePath, filePath);
    }

    public void set(String path, Object object) {
        HolderField field = findField(path);
        if (field == null) {
            throw new IllegalArgumentException("Cannot create new path with set()!");
        }
        ConfigIO.set(this, path, field.getFieldType(), object, field.getField(), field.getComment());
    }

    @Nullable
    private HolderField findField(String path) {
        if (holder == null) return null;
        return Arrays.stream(holder.getFields())
            .filter(field ->
                field.isAnnotationPresent(ConfigPath.class) && field.getAnnotation(ConfigPath.class).value().equalsIgnoreCase(path))
            .map(field -> new HolderField(this, field))
            .findFirst().orElse(null);
    }

    @SneakyThrows
    public void saveConfig() {
        bukkitConfig.save(configFile);
    }

    public void reloadConfig() {
        configFile = null;
        bukkitConfig = null;
        load();
    }

    public static List<EasyConfig> loadInDirectory(String absolutePath, String path) {
        File directory = new File(absolutePath, path);
        if (!directory.isDirectory()) return new ArrayList<>();

        List<EasyConfig> list = new ArrayList<>();

        for (File file : directory.listFiles()) {
            String fileName = file.getName().substring(0, file.getName().lastIndexOf("."));
            EasyConfig config = new EasyConfig(absolutePath, null, path, fileName);
            config.load();
            list.add(config);
        }
        return list;
    }
}
