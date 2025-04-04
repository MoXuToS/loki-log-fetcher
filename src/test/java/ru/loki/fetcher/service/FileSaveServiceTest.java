package ru.loki.fetcher.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileSaveServiceTest {
    private final FileSaveService fileSaveService = new FileSaveService();

    @Test
    public void clearFileContent()
    {
        String testString = "%D0%B3%D1%80%D0%B8%D0%B4test HTTP/1.1";
        assertEquals("test HTTP/1.1", fileSaveService.clearContent(testString));
    }
}
