# Проект "Конвертер валют"

Скрипты sql для инициализации базы находятся по пути: /src/main/resources/db. Инициализация осуществляется liquibase.

Секретный ключ: /src/main/resources/application.properties (Сам ключ - "jUaFBnirgrBKDEqeYaUttEb8A7pHg$QHSCmeEed4").

Для запуска приложения необходимо скачать репозиторий git. В корневой папке проекта при запущенном Docker ввести 
команду в терминале: 

```docker compose up```

Приложение будет запущено на порту 8080.

# Контроллеры:

## Основные методы:

- GET - /officialrates - @RequestParam("date") LocalDate date, @RequestParam("pair") String pair;
- GET - /convert - @RequestParam("from") String from, @RequestParam("to") String to, @RequestParam("amount") double amount;
- POST - /convert - @RequestBody { "from": "USD","to":"UZS","amount":"1000" };
- POST - /setcomission - @RequestBody { "pair":"USD/UZS", "comission":1 };

## Дополнительные методы (для аналитики результатов работы):

- GET - /information - @RequestParam("currency") String currency - информация о счете в выбранной валюте;
- GET - /information/all - список счетов во всех валютах;
- GET - /courses/all - список актуальных на последний день курсов валют;
- GET - /commissions/all - список комиссий перевода валют в UZS и обратно;


