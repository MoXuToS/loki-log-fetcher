package moxutos.loki.fetcher.config;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import feign.Feign;
import feign.Request;
import feign.Util;
import feign.codec.Decoder;
import feign.form.FormEncoder;
import feign.querymap.BeanQueryMapEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import static feign.Util.UTF_8;

@Configuration
public class LokiLogsFeignConfig {

    @Bean
    public Feign.Builder feignBuilder() {
        return Feign.builder()
                .queryMapEncoder(new BeanQueryMapEncoder())
                .encoder(new FormEncoder());
    }

    @Bean
    Request.Options lokiFeignOptions() {
        return new Request.Options(
                5000,
                10000
        );
    }

    @Bean
    public Decoder feignDecoder() {
        return (response, type) -> Util.toString(response.body().asReader(UTF_8));
    }

    @Bean
    public Module javaTimeModule() {
        return new JavaTimeModule();
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
}

