package ru.loki.fetcher.dto.logs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Data
@JsonInclude
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResultDTO {
    private Map<String, String> stream; // Объект с метаданными
    private List<List<String>> values;   // Список записей логов
}
