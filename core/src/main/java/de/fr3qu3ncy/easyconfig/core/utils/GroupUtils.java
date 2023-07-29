package de.fr3qu3ncy.easyconfig.core.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GroupUtils {

    private GroupUtils() {
    }

    public static List<String> createHeader(String... name) {
        List<String> list = new ArrayList<>();
        int decorationLength = Math.max(25, Arrays.stream(name).mapToInt(String::length).max().orElse(10) + 2 + 4);

        //Add first decoration line
        list.add("#".repeat(decorationLength + 1));

        for (String line : name) {
            int lineLength = line.length() + 2;
            int tagLength = (decorationLength - lineLength) / 2;

            list.add("#".repeat(tagLength) + " " + line + " " + "#".repeat(tagLength + 1 +
                //Add 1 if line length is odd
                (lineLength % 2 == 0 ? 1 : 0)));
        }

        //Add last decoration line
        list.add("#".repeat(decorationLength + 1));

        return list;
    }
}
