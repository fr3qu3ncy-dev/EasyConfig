package de.fr3qu3ncy.easyconfig.spigot.serializable;

import de.fr3qu3ncy.easyconfig.core.annotations.ConfigurableField;
import de.fr3qu3ncy.easyconfig.core.serialization.Configurable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@AllArgsConstructor
@NoArgsConstructor
@ConfigurableField
public class SerializablePotionEffect implements Configurable<SerializablePotionEffect> {

    private PotionEffectType type;
    private int duration;
    private int amplifier;

    public PotionEffect toPotionEffect() {
        return new PotionEffect(type, duration, amplifier);
    }
}
