package de.fr3qu3ncy.easyconfig.spigot.serializable;

import de.fr3qu3ncy.easyconfig.core.annotations.ConfigurableField;
import de.fr3qu3ncy.easyconfig.core.annotations.OptionalFields;
import de.fr3qu3ncy.easyconfig.core.serialization.Configurable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import javax.annotation.Nonnull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ConfigurableField
@OptionalFields
public class SerializablePotion implements Configurable<SerializablePotion> {

    private PotionType type;
    private boolean extended;
    private boolean upgraded;
    private int level = 1;

    public SerializablePotion(@Nonnull SerializablePotion other) {
        this.type = other.type;
        this.extended = other.extended;
        this.upgraded = other.upgraded;
        this.level = other.level;
    }

    public void apply(PotionMeta potionMeta) {
        if (type == null) return;
        potionMeta.setBasePotionData(new PotionData(type, extended, upgraded || level > 1));
    }
}
