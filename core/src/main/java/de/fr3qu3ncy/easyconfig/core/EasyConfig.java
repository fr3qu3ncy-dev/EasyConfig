package de.fr3qu3ncy.easyconfig.core;

import de.fr3qu3ncy.easyconfig.core.configuration.AdditionalInformationWriter;
import de.fr3qu3ncy.easyconfig.core.configuration.ConfigField;
import de.fr3qu3ncy.easyconfig.core.configuration.FileConfiguration;
import de.fr3qu3ncy.easyconfig.core.configuration.StringFormatter;
import de.fr3qu3ncy.easyconfig.core.io.ConfigIO;
import de.fr3qu3ncy.easyconfig.core.utils.ReflectionUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.logging.Logger;

@Getter
public abstract class EasyConfig {

    @Setter
    private boolean debug = false;

    private FileConfiguration fileConfig;
    private final File configDirectory;
    private final Class<?> holdingClass;

    @Nullable
    @Setter
    private Object holderInstance;
    private final String fileName;
    private final ConfigIO configIO = new ConfigIO(this);
    private final AdditionalInformationWriter infoWriter = new AdditionalInformationWriter(this);
    private final Logger logger = Logger.getLogger("EasyConfig");

    private File configFile;

    protected EasyConfig(FileConfiguration fileConfig, File configDirectory, Class<?> holdingClass, String fileName,
                         @Nullable Object holderInstance) {
        this.fileConfig = fileConfig;
        this.configDirectory = configDirectory;
        this.holdingClass = holdingClass;
        this.fileName = fileName;
        this.holderInstance = holderInstance;
    }

    protected EasyConfig(FileConfiguration fileConfig, File configDirectory, Class<?> holdingClass, String fileName) {
        this(fileConfig, configDirectory, holdingClass, fileName, null);
    }

    public void debug(String message) {
        if (!debug) return;
        logger.info(message);
    }

    public void load() {
        load(true);
    }

    /**
     * This will attempt to create all needed files and directories and then load this EasyConfig.
     */
    public void load(boolean saveDefaults) {
        //Create config file and directories
        createConfig();

        //Cannot load any values if there is no holding class
        if (holdingClass == null) return;

        //Load all configurable fields in this class
        ReflectionUtils.getAllFields(holdingClass).stream()
            .filter(ReflectionUtils::isValidConfigurableField)
            .map(field -> new ConfigField<>(field, getBlankDefaultInstance()))
            .forEach(configField -> loadConfigField(configField, saveDefaults));

        //Finally, replace all comments, groups and headers
        infoWriter.replaceCommentsAndGroups();
    }

    public void loadFromInstance() {
        load(false);
        if (holderInstance == null) return;

        ReflectionUtils.getAllFields(holdingClass).stream()
            .filter(ReflectionUtils::isValidConfigurableField)
            .forEach(field -> {
                String fieldName = field.getName();
                try {
                    field.setAccessible(true);
                    Object value = field.get(holderInstance);

                    configIO.set(fieldName, value);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            });
    }

    public Object getBlankDefaultInstance() {
        if (holderInstance == null) return null;
        try {
            return holderInstance.getClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Field[] getFields() {
        return ReflectionUtils.getAllFields(holdingClass).stream()
            .filter(ReflectionUtils::isValidConfigurableField)
            .toArray(Field[]::new);
    }

    private <T> void loadConfigField(ConfigField<T> configField, boolean saveDefaults) {
        debug("Loading configurable field " + configField.getField().getName() +
            " in class " + configField.getField().getDeclaringClass().getName());

        String path = configField.getPath();

        //Check if field has a default value that is not yet saved into the file (so it's not overridden)
        //Otherwise check if path is already contained in config, if yes write it to the field
        if (configField.getDefaultValue() != null && !fileConfig.contains(path) && saveDefaults) {
            //Log debug
            debug("Saving default value " + configField.getDefaultValue() + "...");

            //Serialize default value to config, then read it back
            configIO.writeToConfig(configField);
            configIO.readToField(configField);
        } else if (fileConfig.contains(path)) {
            configIO.readToField(configField);
        }
    }

    @SneakyThrows
    private void createConfig() {
        if (configFile != null) return;

        //Create the needed directory
        if (!configDirectory.exists() && !configDirectory.mkdirs()) {
            throw new IOException("Couldn't create config directory!");
        }

        //Create the config file
        configFile = new File(configDirectory, fileName + ".yml");
        if (!configFile.exists() && !configFile.createNewFile()) {
            throw new IOException("Couldn't create config file!");
        }

        //Set line width for comments
        this.fileConfig.setMaxWidth(128);
    }

    public void saveConfig() {
        fileConfig.save(configFile);
    }

    public boolean existsConfigFile() {
        return new File(configDirectory, fileName + ".yml").exists();
    }

    protected abstract FileConfiguration reloadInternal(File configDirectory, String fileName);

    public void reloadConfig() {
        this.fileConfig = reloadInternal(configDirectory, fileName);
        load();
    }

    public abstract StringFormatter getStringFormatter();
}
