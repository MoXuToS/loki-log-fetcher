package ru.loki.fetcher.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.loki.fetcher.config.LokiRequestDTOConfig;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

@ExtendWith(MockitoExtension.class)
public class LokiRequestDTOTest {
    @Mock
    private LokiRequestDTOConfig config;

    @Test
    public void copyTest() {
        LokiRequestDTO original = LokiRequestDTO.create(config);
        LokiRequestDTO copy = original.copy();
        copy.setInstance("test-instance");
        assertNotEquals(original, copy);
    }
}
