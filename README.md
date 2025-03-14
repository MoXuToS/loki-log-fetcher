# Loki Log Fetcher

Application for exporting and saving logs from [Grafana Loki](https://grafana.com/oss/loki/) with time range and label filtering capabilities.

## Features
- Log querying via Loki API
- Time range filtering
- Label-based filtering
- Automatic log sorting by timestamp
- File output with pod source information
- Infinite loop protection

## Requirements
- Java 8+
- Maven
- Access to Loki server
- Spring Boot 2.7+

-----------------------------------------------------------------------------------------
# Loki logs getter

Приложение для выгрузки и сохранения логов из [Grafana Loki](https://grafana.com/oss/loki/) с возможностью фильтрации по времени и меткам.

## Особенности
- Запрос логов через Loki API
- Поддержка временных диапазонов
- Фильтрация по меткам (labels)
- Автоматическая сортировка логов по времени
- Сохранение в файл с указанием pod-источника
- Защита от бесконечных циклов

## Требования
- Java 8+
- Maven
- Доступ к серверу Loki
- Spring Boot 2.7+
