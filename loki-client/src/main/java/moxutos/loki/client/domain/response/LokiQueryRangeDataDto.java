package moxutos.loki.client.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import moxutos.loki.client.enums.LokiResultType;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LokiQueryRangeDataDto {

    LokiResultType resultType;
    List<LokiResultDto> result;
}
