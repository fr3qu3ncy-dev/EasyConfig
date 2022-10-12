package de.fr3qu3ncy.easyconfignew.core.utils;

import de.fr3qu3ncy.easyconfignew.core.configuration.ConfigField;
import de.fr3qu3ncy.easyconfignew.core.io.DataWriter;

public class ConfigUtils {

    public static final String COMMENT_START_IDENTIFIER = "_COMMENT_START_";
    public static final String COMMENT_IDENTIFIER = "_COMMENT_";
    public static final String GROUP_IDENTIFIER = "_GROUP_";
    public static final String GROUP_HEADER_IDENTIFIER = "_GROUP_HEADER_";

    private ConfigUtils() {}

    public static void saveFieldInformation(DataWriter writer, ConfigField<?> configField) {
        String path = configField.getPath();

        //Save comment
        String comment = configField.getComment();
        if (comment != null) {
            handleMultiLineComment(writer, path, comment);
        }

        //Save group
        String group = configField.getGroup();
        if (group != null) {
            writer.writeData(path + GROUP_IDENTIFIER, group);
        }

        //Save group header
        if (configField.isWriteGroupHeader() && group != null) {
            handleMultiLineHeader(writer, path, group);
        }
    }

    private static void handleMultiLineComment(DataWriter writer, String path, String comment) {
        int i = 0;
        for (String line : comment.split("\n")) {
            writer.writeData(path + "_" + i + "_" +
                (i == 0 ? COMMENT_START_IDENTIFIER : COMMENT_IDENTIFIER), line);
            i++;
        }
    }

    private static void handleMultiLineHeader(DataWriter writer, String path, String header) {
        int i = 0;
        for (String line : GroupUtils.createHeader(header.split("\n"))) {
            writer.writeData(path + "_" + i + "_" + GROUP_HEADER_IDENTIFIER, line);
            i++;
        }
    }

}
