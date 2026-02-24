package core;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RLEParser {
    public static void load(String filePath, Grid grid) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int x = 0, y = 0;
            boolean headerRead = false;
            StringBuilder rleData = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                if (!headerRead && line.startsWith("x")) {
                    headerRead = true;
                    continue;
                }

                rleData.append(line);
                if (line.endsWith("!")) break;
            }

            parseRLEData(rleData.toString(), grid);
        }
    }

    private static void parseRLEData(String data, Grid grid) {
        Pattern pattern = Pattern.compile("(\\d*)([bo$!])");
        Matcher matcher = pattern.matcher(data);

        int currentX = 0;
        int currentY = 0;

        while (matcher.find()) {
            int count = matcher.group(1).isEmpty() ? 1 : Integer.parseInt(matcher.group(1));
            char type = matcher.group(2).charAt(0);

            switch (type) {
                case 'b':
                    currentX += count;
                    break;
                case 'o':
                    for (int i = 0; i < count; i++) {
                        grid.setCell(currentX++, currentY, true);
                    }
                    break;
                case '$':
                    currentX = 0;
                    currentY += count;
                    break;
                case '!':
                    return;
            }
        }
    }
}
