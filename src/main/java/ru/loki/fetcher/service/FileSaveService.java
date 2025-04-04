package ru.loki.fetcher.service;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

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

    public String clearContent(String str) {
        return str.replaceAll("%[A-Fa-f0-9]{2}", "");
    }

    /**
     * Метод для сохранения данных в файл
     *
     * @param content Строка для сохранения в файл
     * @param filename Название файла куда сохранять
     */
    public void saveToFile(String content, String filename) {
        try {
            if (content == null) {
                throw new IllegalArgumentException("Нечего сохранять в файл");
            }
            String filePath = folder + "/" + filename + ".log";

            // Кириллица в запросах
            String sanitizedContent = content.replaceAll("%[A-Fa-f0-9]{2}", "");

            try (BufferedWriter writer = Files.newBufferedWriter(
                    Paths.get(filePath),
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            )) {
                writer.write(content);
                writer.newLine();
            }
        } catch (Exception e) {
            log.error("Ошибка: {}", filename, e);
            throw new RuntimeException(e);
        }
    }
}
