package ru.loki.fetcher.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Data
@Getter
@Setter
@JsonInclude
@JsonIgnoreProperties(ignoreUnknown = true)
public class LokiResponseDTO {
    private String status;
    private ResponseDataDTO data;
}
