package de.fr3qu3ncy.easyconfig.spigot.serializers;

import de.fr3qu3ncy.easyconfig.core.io.DataSource;
import de.fr3qu3ncy.easyconfig.core.io.DataWriter;
import de.fr3qu3ncy.easyconfig.core.serialization.ConfigSerializer;
import de.fr3qu3ncy.easyconfig.core.serialization.SerializationInfo;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class MaterialSerializer implements ConfigSerializer<Material> {

    @Override
    public void serialize(String path, DataWriter dataWriter, @Nonnull Material material, SerializationInfo<? extends Material> serializationInfo) {
        dataWriter.writeData(path, material.getKey().getKey());
    }

    @Override
    public Material deserialize(String path, DataSource dataSource, SerializationInfo<Material> serializationInfo) {
        String key = dataSource.getData(path);
        if (key == null) return null;

        return Material.matchMaterial(key);
    }
}
