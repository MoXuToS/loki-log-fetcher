package ru.loki.fetcher.service;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@NoArgsConstructor
public class FileSaveService {

    /**
     * Метод для очистки строки от запрещенных символов Windows
     *
     * @param input Строка названия файла
     */
    public static String clearFilename(String input) {
        return input.replaceAll("[\\\\/:*?\"<>|]", "_"); // Замена запрещенных символов на _
    }

    /**
     * Метод для сохранения данных в файл
     *
     * @param content Строка для сохранения в файл
     * @param filename Название файла куда сохранять
     */
    public void saveToFile(String content, String filename) {
        try {
            Files.write(
                    Paths.get(filename),
                    (content + System.lineSeparator()).getBytes(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            System.err.println("Ошибка: " + e.getMessage());
        }
    }
}
