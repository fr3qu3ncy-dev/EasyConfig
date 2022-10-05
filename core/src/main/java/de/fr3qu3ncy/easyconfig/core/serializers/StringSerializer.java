package de.fr3qu3ncy.easyconfig.core.serializers;

import de.fr3qu3ncy.easyconfig.core.EasyConfig;
import de.fr3qu3ncy.easyconfig.core.SerializationInfo;
import de.fr3qu3ncy.easyconfig.core.data.DataSource;
import de.fr3qu3ncy.easyconfig.core.data.DataWriter;
import de.fr3qu3ncy.easyconfig.core.serialization.ConfigSerializer;

import javax.annotation.Nonnull;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringSerializer implements ConfigSerializer<String> {

    @Override
    public void serialize(@Nonnull SerializationInfo<?> info, DataWriter writer, @Nonnull String value) {
        writer.writeData(value);
    }

    @Override
    public String deserialize(@Nonnull SerializationInfo<?> info, DataSource source) {
        return formatColors(info.config(), source.getData());
    }

    private static String formatColors(EasyConfig config, String message) {
        Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
        Matcher matcher = pattern.matcher(message);

        while (matcher.find()) {
            String color = message.substring(matcher.start(), matcher.end());
            message = message.replace(color, config.getPreferences().stringFormatter().replaceColor(color));
            matcher = pattern.matcher(message);
        }
        return config.getPreferences().stringFormatter().translateAlternateColorCodes('&', message);
    }
}
