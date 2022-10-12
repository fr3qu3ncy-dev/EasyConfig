package de.fr3qu3ncy.easyconfignew.core.serializers;

import de.fr3qu3ncy.easyconfignew.core.EasyConfig;
import de.fr3qu3ncy.easyconfignew.core.io.DataSource;
import de.fr3qu3ncy.easyconfignew.core.io.DataWriter;
import de.fr3qu3ncy.easyconfignew.core.serialization.ConfigSerializer;
import de.fr3qu3ncy.easyconfignew.core.serialization.SerializationInfo;

import javax.annotation.Nonnull;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringSerializer implements ConfigSerializer<String> {

    private static String formatColors(EasyConfig config, String message) {
        Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
        Matcher matcher = pattern.matcher(message);

        while (matcher.find()) {
            String color = message.substring(matcher.start(), matcher.end());
            message = message.replace(color, config.getStringFormatter().replaceColor(color));
            matcher = pattern.matcher(message);
        }
        return config.getStringFormatter().translateAlternateColorCodes('&', message);
    }

    @Override
    public void serialize(String path, DataWriter writer, @Nonnull String value, SerializationInfo<String> info) {
        writer.writeData(path, value);
    }

    @Override
    public String deserialize(String path, DataSource source, SerializationInfo<String> info) {
        return formatColors(info.config(), source.getData(path));
    }
}
