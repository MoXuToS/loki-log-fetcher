package ru.loki.fetcher.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@NoArgsConstructor
@Setter
@Slf4j
public class FileSaveService {
    private String folder;

    /**
     * Метод для очистки строки от запрещенных символов Windows
     *
     * @param input Строка названия файла
     */
    public static String clearFilename(String input) {
        return input.replaceAll("[\\\\/:*?.\"<>|]", "_"); // Замена запрещенных символов на _
    }

    /**
     * Создаем папку под логи
     *
     * @param folderName Название папки
     */
    public void createFolder(String folderName) throws IOException {
        this.setFolder(folderName);
        Files.createDirectories(Paths.get(folder));
    }

    public byte[] parseLogContent(String logContent) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Pattern pattern = Pattern.compile("\\[0x([A-Fa-f0-9]{2})]");
        Matcher matcher = pattern.matcher(logContent);

        int lastIndex = 0;
        while (matcher.find()) {
            bos.write(logContent.substring(lastIndex, matcher.start()).getBytes(StandardCharsets.UTF_8));

            String hex = matcher.group(1);
            bos.write((byte) Integer.parseInt(hex, 16));

            lastIndex = matcher.end();
        }

        bos.write(logContent.substring(lastIndex).getBytes(StandardCharsets.UTF_8));

        return bos.toByteArray();
    }

    /**
     * Метод для сохранения данных в файл
     *
     * @param content Строка для сохранения в файл
     * @param filename Название файла куда сохранять
     */
    public void saveToFile(byte[] content, String filename) {
        try {
            Path dirPath = Paths.get(folder);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }

            Path filePath = dirPath.resolve(filename + ".log");

            // Автоматическое добавление переноса строки
            byte[] separator = System.lineSeparator().getBytes(StandardCharsets.UTF_8);
            byte[] dataWithSeparator = ByteBuffer.allocate(content.length + separator.length)
                    .put(content)
                    .put(separator)
                    .array();

            Files.write(filePath, dataWithSeparator, StandardOpenOption.CREATE, StandardOpenOption.APPEND);

        } catch (IOException e) {
            log.error("Ошибка сохранения файла: {}", filename, e);
            throw new UncheckedIOException(e);
        }
    }
}
