package de.fr3qu3ncy.easyconfig.core.io;

import de.fr3qu3ncy.easyconfig.core.ConfigIO;
import de.fr3qu3ncy.easyconfig.core.EasyConfig;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.file.Files;

@RequiredArgsConstructor
public class ConfigWriter {

    private final EasyConfig config;

    private String lastLine;
    private String currentGroup = "";

    @SneakyThrows
    public void replaceCommentsAndGroups() {
        //Cannot replace comments in non-existing file
        if (config.getConfigFile() == null) return;

        File configFile = config.getConfigFile();

        //Create copy of old file
        File oldConfig = new File(config.getFileDirectory(), config.getFileName() + ".old.yml");
        if (!oldConfig.exists() && !oldConfig.createNewFile()) return;
        FileUtils.copyFile(configFile, oldConfig);

        try (BufferedReader reader = new BufferedReader(new FileReader(oldConfig)); FileWriter writer = new FileWriter(configFile)) {

            String line;
            while ((line = reader.readLine()) != null) {
                writeLine(writer, line);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        Files.delete(oldConfig.toPath());
    }

    private void writeLine(FileWriter writer, String line) throws IOException {
        boolean groupChanged = false;

        String path = line.split(":")[0].replace(" ", "");
        int startIndex = findStartIndex(line);

        String toWrite = line;

        if (path.contains(ConfigIO.COMMENT_IDENTIFIER) || path.contains(ConfigIO.COMMENT_START_IDENTIFIER)) {

            //Check if line has comment definition
            String comment = writeComment(startIndex, line, 0, 0);
            if (comment != null) toWrite = comment;

            //Write empty line before comment if last line was not empty and was not the start of this comment
            if (lastLine != null && !lastLine.isBlank() && !lastLine.contains(ConfigIO.COMMENT_START_IDENTIFIER)) {
                toWrite = "\n" + toWrite;
            }
        } else if (path.contains(ConfigIO.GROUP_HEADER_IDENTIFIER)) {

            //Check if this line has a group header definition
            String header = writeComment(startIndex, line, 1, 1);
            if (header != null) toWrite = header;

        } else if (path.contains(ConfigIO.GROUP_IDENTIFIER)) {

            //Check if line has group definition
            String group = readGroup(startIndex, line);
            if (!group.equalsIgnoreCase(currentGroup)) {
                groupChanged = true;
                currentGroup = group;
            }
        }

        //Write empty line if group has changed, and it's not the first line
        //Terminate if group has changed, there's nothing to write
        if (groupChanged) {
            groupChanged(writer);
            return;
        }
        writer.write(toWrite + "\n");
        lastLine = line;
    }

    private int findStartIndex(String line) {
        int startIndex = -1;
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) != ' ') {
                startIndex = i;
                break;
            }
        }
        return startIndex;
    }

    private String writeComment(int startIndex, String line, int startOffset, int endOffset) {
        if (startIndex != -1) {
            StringBuilder sb = new StringBuilder();
            sb.append(" ".repeat(startIndex));
            sb.append("#").append(line, line.indexOf(":") + 2 + startOffset, line.length() - endOffset);
            return sb.substring(0, Math.min(128, sb.length()));
        }
        return null;
    }

    private String readGroup(int startIndex, String line) {
        if (startIndex == -1) return "";

        return line.substring(line.indexOf(":") + 2);
    }

    @SneakyThrows
    private void groupChanged(FileWriter writer) {
        if (lastLine != null) {
            writer.write("\n");
            lastLine = "";
        }
    }
}
