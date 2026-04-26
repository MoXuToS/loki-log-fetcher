package moxutos.loki.client.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import moxutos.loki.client.domain.response.LokiValueDto;

import java.io.IOException;
import java.util.List;

/**
 * Десериализатор ответа.
 * <a href="https://grafana.com/docs/loki/latest/reference/loki-http-api/#step-versus-interval">Loki HTTP API</a>
 */
public class LokiValueDeserializer extends JsonDeserializer<LokiValueDto> {

    @Override
    public LokiValueDto deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        List<String> list = p.readValueAs(new TypeReference<List<String>>() {});

        return new LokiValueDto(
                Long.parseLong(list.get(0)),
                list.get(1)
        );
    }
}
