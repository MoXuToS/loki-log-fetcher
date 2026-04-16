package ru.loki.fetcher.dto.logs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LogsResponseDTO {
    private String resultType;
    private List<ResultDTO> result;
}
