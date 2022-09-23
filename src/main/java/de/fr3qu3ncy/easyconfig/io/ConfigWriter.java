package de.fr3qu3ncy.easyconfig.io;

import de.fr3qu3ncy.easyconfig.ConfigIO;
import de.fr3qu3ncy.easyconfig.EasyConfig;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.file.Files;

@AllArgsConstructor
public class ConfigWriter {

    private final EasyConfig config;

    @SneakyThrows
    public void replaceComments() {
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
        String path = line.split(":")[0].replace(" ", "");
        if (path.contains(ConfigIO.COMMENT_IDENTIFIER)) {
            int startIndex = -1;
            for (int i = 0; i < line.length(); i++) {
                if (line.charAt(i) != ' ') {
                    startIndex = i;
                    break;
                }
            }
            if (startIndex != -1) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0 ; i < startIndex ; i++) {
                    sb.append(" ");
                }
                sb.append("#").append(line.substring(line.indexOf(":") + 2));
                line = sb.substring(0, Math.min(128, sb.length()));
            }
        }
        writer.write(line + "\n");
    }
}
