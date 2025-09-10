# Документация по развертыванию проекта

## 1. Назначение
Spring Boot сервис (teamwork) предоставляет бизнес‑логику рекомендаций и вспомогательные эндпоинты.

## 2. Требования окружения
- JDK: 21
- Maven 3.9+
- Доступ к PostgreSQL (детали в apllication.properties)
- Доступ к H2 базе (transaction.mv.db) в корне проекта или требуется настройка пути

Проверка версий:
```bash
java -version
mvn -version
```

## 3. Сборка

Команда сборки:
```bash
mvn clean package
```
либо из IntelliJ IDEA: Maven Projects → Lifecycle → clean, затем package.

Результат: `target/teamwork-0.0.1-SNAPSHOT.jar`.

## 4. Запуск
### Вариант A: Из IntelliJ IDEA
1. Открыть проект как Maven Project.
2. Убедиться что выбран SDK 21.
3. Добавить переменные среды (см. раздел 6).
4. Run/Debug конфигурация: класс `ru.skypro.teamwork.TeamworkApplication`.

### Вариант B: Запуск JAR
Перейти в корень модуля (чтобы относительные пути БД указывали правильно) и выполнить:
```bash
java -jar target/teamwork-0.0.1-SNAPSHOT.jar --telegram.bot.token=XXXX
```
Если запускаете командой из другой директории, используйте абсолютные пути к файлам баз, а так-же к JAR.

PowerShell пример с явным путём Java:
```powershell
& "C:\Users\<user>\.jdks\jbr-21.0.8\bin\java.exe" -jar target\teamwork-0.0.1-SNAPSHOT.jar --telegram.bot.token=XXXX
```

## 5. Эндпоинт /management/info
Должен возвращать:
```json
{
  "name": "teamwork",
  "version": "0.0.1-SNAPSHOT"
}
```
Источник данных — бин `BuildProperties`. Требование для его появления: выполнен `mvn package` (создан build-info). Если при старте ошибка `BuildProperties bean not found` — не собран пакет или отсутствует goal build-info.

## 6. Переменные окружения
Основные:
- `telegram.bot.token` 
Задаётся или через переменную окружения `TELEGRAM_BOT_TOKEN`, либо через run/debug конфигурацию, либо прямо аргументом `--telegram.bot.token=XXXX` при запуске JAR.

## 7. Конфигурация баз данных
### PostgreSQL (основная)
Пример в `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/app
spring.datasource.username=app
spring.datasource.password=secret
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=false
```
Liquibase применится автоматически (см. раздел 8).

### H2 (файловая)
`jdbc:h2:file:./transaction`. Если JAR запускается из `target`, создастся БД в `target/transaction.mv.db`. Чтобы использовать файл в корне проекта:
1. Запускайтесь из корня модуля.
2. Или меняйте URL на `jdbc:h2:file:../transaction` при запуске из `target`.

### Типичная причина ошибки "Table X not found"
Путь БД указывает на другой физический файл (пустой). Убедитесь, что относительный путь совпадает с рабочей директорией процесса.

## 8. Миграции Liquibase
- Чтобы принудительно пересоздать схему (в тестовом окружении) можно очистить файл H2 или БД PostgreSQL.

Проверка миграций отдельно:
```bash
mvn liquibase:update
```

## 9. Логи и проверка успешного запуска
Признаки успеха:
- `Tomcat started on port 8080`.
- `Started TeamworkApplication`.
- Для PostgreSQL: строки Liquibase и HikariPool добавил соединение.
- Для H2: `HikariPool-X - Added connection ... jdbc:h2:file:...`.
- Для Telegram: сообщение о старте бота.

Проверка /management/info:
```bash
curl http://localhost:8080/management/info
```

## 10. Тесты
Запуск:
```bash
mvn test
```
Если тест `contextLoads` падает из-за отсутствия `TELEGRAM_BOT_TOKEN`, можно:
- Задать переменную в конфигарции run/debug либо в окружении перед запуском тестов.
