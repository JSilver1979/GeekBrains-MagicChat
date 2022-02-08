package client;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class ChatHistory {
    private static File file;

    public static void createFile(String login) throws IOException {
        file = new File("./history_" + login + ".txt");
        file.createNewFile();
    }

    public static String readHistory() throws IOException {
        StringBuffer stringBuffer = new StringBuffer();
        List<String> list = Files.readAllLines(file.getAbsoluteFile().toPath());

        if (list.size() < 100) {
            for (String s : list) {
                stringBuffer.append(s + "\n");
            }
        } else {
            for (int i = list.size() - 100; i < list.size(); i++) {
                stringBuffer.append(list.get(i) + "\n");
            }
        }
        return stringBuffer.toString();
    }

    public static FileWriter writeHistory() throws IOException {
        FileWriter writer = new FileWriter(file.getAbsoluteFile(), true);
        return writer;
    }
}
