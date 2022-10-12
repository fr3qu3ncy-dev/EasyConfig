package de.fr3qu3ncy.easyconfignew.core;

import de.fr3qu3ncy.easyconfignew.core.configuration.AdditionalInformationWriter;
import de.fr3qu3ncy.easyconfignew.core.configuration.ConfigField;
import de.fr3qu3ncy.easyconfignew.core.configuration.StringFormatter;
import de.fr3qu3ncy.easyconfignew.core.io.ConfigIO;
import de.fr3qu3ncy.easyconfignew.core.configuration.FileConfiguration;
import de.fr3qu3ncy.easyconfignew.core.utils.ReflectionUtils;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Logger;

@Getter
public abstract class EasyConfig {

    private final FileConfiguration fileConfig;
    private final File configDirectory;
    private final Class<?> holdingClass;
    private final String fileName;
    private final ConfigIO configIO = new ConfigIO(this);
    private final AdditionalInformationWriter infoWriter = new AdditionalInformationWriter(this);
    private final Logger logger = Logger.getLogger("EasyConfig");

    private File configFile;

    protected EasyConfig(FileConfiguration fileConfig, File configDirectory, Class<?> holdingClass, String fileName) {
        this.fileConfig = fileConfig;
        this.configDirectory = configDirectory;
        this.holdingClass = holdingClass;
        this.fileName = fileName;
    }

    /**
     * This will attempt to create all needed files and directories and then load this EasyConfig.
     */
    public void load() {
        //Create config file and directories
        createConfig();

        //Cannot load any values if there is no holding class
        if (holdingClass == null) return;

        //Load all configurable fields in this class
        Arrays.stream(holdingClass.getFields())
            .filter(ReflectionUtils::isValidConfigurableField)
            .map(ConfigField::new)
            .forEach(this::loadConfigField);

        //Finally, replace all comments, groups and headers
        infoWriter.replaceCommentsAndGroups();
    }

    private <T> void loadConfigField(ConfigField<T> configField) {
        logger.info("Loading configurable field " + configField.getField().getName() +
            " in class " + configField.getField().getDeclaringClass().getName());

        String path = configField.getPath();

        //Check if field has a default value that is not yet saved into the file (so it's not overridden)
        //Otherwise check if path is already contained in config, if yes write it to the field
        if (configField.getDefaultValue() != null && !fileConfig.contains(path)) {
            //Log debug
            logger.info("Saving default value " + configField.getDefaultValue() + "...");

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

    public abstract StringFormatter getStringFormatter();
}
