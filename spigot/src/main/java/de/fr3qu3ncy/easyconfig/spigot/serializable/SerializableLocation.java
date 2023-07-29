package de.fr3qu3ncy.easyconfig.spigot.serializable;

import de.fr3qu3ncy.easyconfig.core.annotations.ConfigurableField;
import de.fr3qu3ncy.easyconfig.core.serialization.Configurable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ConfigurableField
public class SerializableLocation implements Configurable<SerializableLocation> {

    private String world;
    private int x;
    private int y;
    private int z;

    public SerializableLocation(Location location) {
        this(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public Location toLocation() {
        return new Location(Bukkit.getWorld(world), x, y, z);
    }
}