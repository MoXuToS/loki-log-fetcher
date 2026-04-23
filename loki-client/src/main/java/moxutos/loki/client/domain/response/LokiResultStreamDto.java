package moxutos.loki.client.domain.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import moxutos.loki.client.enums.LokiStreamType;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LokiResultStreamDto {

    String app;

    String container;

    @JsonAlias("detected_level")
    String detectedLevel;

    String env;

    String filename;

    String instance;

    String pod;

    @JsonAlias("service_name")
    String serviceName;

    LokiStreamType stream;

    String system;
}
