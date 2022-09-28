package de.fr3qu3ncy.easyconfig.serializers;

import de.fr3qu3ncy.easyconfig.SerializationInfo;
import de.fr3qu3ncy.easyconfig.data.DataSource;
import de.fr3qu3ncy.easyconfig.data.DataWriter;
import de.fr3qu3ncy.easyconfig.serialization.ConfigSerializer;

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
        return formatColors(source.getData());
    }

    private static String formatColors(String message) {
        Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
        Matcher matcher = pattern.matcher(message);

        while (matcher.find()) {
            String color = message.substring(matcher.start(), matcher.end());
            message = message.replace(color, net.md_5.bungee.api.ChatColor.of(color) + "");
            matcher = pattern.matcher(message);
        }
        return net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', message);
    }
}
