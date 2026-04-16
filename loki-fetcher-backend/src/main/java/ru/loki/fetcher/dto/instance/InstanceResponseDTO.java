package ru.loki.fetcher.dto.instance;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude
@JsonIgnoreProperties(ignoreUnknown = true)
public class InstanceResponseDTO {
    String pod;
}
