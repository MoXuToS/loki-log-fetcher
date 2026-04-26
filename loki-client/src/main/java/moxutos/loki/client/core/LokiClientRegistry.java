package moxutos.loki.client.core;

import lombok.RequiredArgsConstructor;
import moxutos.loki.client.rest.LokiHttpClientApi;

import java.util.Collections;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Репозиторий клиентов http клиентов grafana loki.
 */
@RequiredArgsConstructor
public class LokiClientRegistry {

    private final Map<String, LokiHttpClientApi> clients;

    /**
     * Получить клиент loki.
     * @param name наименование клиента.
     * @return httpClient loki.
     */
    public LokiHttpClientApi get(String name) {
        LokiHttpClientApi client = clients.get(name);
        if (client == null) {
            throw new NoSuchElementException("Не найден клиент для loki: " + name);
        }
        return client;
    }

    /**
     * Получить список всех известных источников Loki.
     * @return все известные источники loki.
     */
    public Set<String> names() {
        return Collections.unmodifiableSet(clients.keySet());
    }

    public Map<String, LokiHttpClientApi> asMap() {
        return Collections.unmodifiableMap(clients);
    }

    /**
     * Проверить если ты такой источник loki.
     * @param name наименование источника.
     * @return есть ли такой источник данных.
     */
    public boolean contains(String name) {
        return clients.containsKey(name);
    }
}