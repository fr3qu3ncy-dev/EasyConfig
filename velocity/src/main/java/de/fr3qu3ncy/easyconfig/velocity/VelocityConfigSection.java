package de.fr3qu3ncy.easyconfig.velocity;

import de.fr3qu3ncy.easyconfig.core.configuration.FileConfiguration;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;
import org.yaml.snakeyaml.DumperOptions;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class VelocityConfigSection implements FileConfiguration {

    private final ConfigurationNode node;

    @Override
    public boolean contains(String path) {
        return !getLastNode(path).isEmpty();
    }

    @Override
    public <T> T get(String path) {
        return (T) getLastNode(path).getValue();
    }

    @Override
    public <T> T get(String path, T defaultValue) {
        return (T) getLastNode(path).getValue(defaultValue);
    }

    @Override
    public void set(String path, Object object) {
        getLastNode(path).setValue(object);
    }

    @Override
    public FileConfiguration getChild(String path) {
        return new VelocityConfigSection(getLastNode(path));
    }

    @Override
    public List<String> getKeys() {
        try {
            Map<Object, Object> value = (Map<Object, Object>) node.getValue();
            if (value == null) return Collections.emptyList();
            return value.keySet().stream().map(String.class::cast).toList();
        } catch (ClassCastException ex) {
            return Collections.emptyList();
        }
    }

    @Override
    public void setMaxWidth(int width) {
        /* Not implemented on Velocity */
    }

    @Override
    @SneakyThrows
    public void save(File file) {
        YAMLConfigurationLoader.builder()
            .setFile(file)
            .setFlowStyle(DumperOptions.FlowStyle.BLOCK)
            .setIndent(4)
            .build().save(node);
    }

    private ConfigurationNode getLastNode(String path) {
        String[] children = path.split("\\.");
        ConfigurationNode finalNode = node;
        for (String child : children) {
            finalNode = finalNode.getNode(child);
        }
        return finalNode;
    }
}
