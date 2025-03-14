package ru.loki.fetcher.config;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import feign.Feign;
import feign.Request;
import feign.Util;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.form.FormEncoder;
import feign.querymap.BeanQueryMapEncoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

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
    Request.Options zabbixFeignOptions() {
        return new Request.Options(
                5000,
                10000
        );
    }

    @Bean
    public Encoder feignEncoder() {
        return new SpringEncoder(() ->
                new HttpMessageConverters(
                        new MappingJackson2HttpMessageConverter()
                )
        );
    }

    @Bean
    public Decoder feignDecoder() {
        return (response, type) -> {
            String body = Util.toString(response.body().asReader(UTF_8));
            return body; // Возвращаем сырой JSON как строку
        };
    }

    private ObjectFactory<HttpMessageConverters> feignHttpMessageConverter() {
        return () -> new HttpMessageConverters(
                new MappingJackson2HttpMessageConverter()
        );
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

