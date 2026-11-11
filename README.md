# SDET Practicum: UI Test Automation

Автотесты UI для интернет-магазина [automationteststore.com](https://automationteststore.com/). Проект покрывает каталог, сортировку товаров, поиск и работу с корзиной.

## Что проверяется

### 1. Сортировка товаров в категории — `CategoryTest`

- Берётся список категорий, `HOME` исключается.
- Находится первая категория минимум с 4 товарами.
- Проверяются все четыре вида сортировки: `Name A - Z`, `Name Z - A`, `Price Low > High`, `Price High > Low`.

### 2. Поиск и корзина — `SearchAndCartTest`

- Поиск по запросу `shirt` и сортировка `Name A - Z`.
- Добавление двух товаров со случайным количеством.
- Поиск самого дешёвого товара в корзине и удвоение его количества.
- Пересчёт ожидаемой суммы и сверка с фактической (погрешность 0.01).

### 3. Случайные товары и удаление позиций — `CartTest`

- Добавление 5 случайных товаров со случайным количеством (1–7).
- Расчёт ожидаемой суммы оставшихся товаров.
- Удаление каждого второго товара (позиции 2, 4, …) от конца к началу.
- Сверка итоговой суммы после удаления.

## Стек

- Java 17
- Maven
- Selenium WebDriver 4
- JUnit 6 (Jupiter)
- Page Object
- Allure (Epic / Feature / Story / Severity, скриншот при падении)
- WebDriverManager
- `ThreadLocal` для thread-safe работы WebDriver

## Структура проекта

```text
.github/workflows/ui-tests.yml        # CI: сборка, прогон, Allure-артефакт
src/test/java/
├── helpers/ParameterProvider.java    # чтение config.properties
├── pages/                            # Page Object + статические шаги
├── tests/                            # BaseTest и три теста
└── utils/                            # WebDriverFactory, генерация данных
src/test/resources/configurations/config.properties
```

## Запуск

Требования: JDK 17, Maven, установленный Google Chrome.

```bash
mvn clean test
```

Отчёт Allure:

```bash
mvn allure:serve
```

Пример отчёта — в [`screenshots/AllureScreenshot.png`](screenshots/AllureScreenshot.png).

Запуск в headless-режиме (как в CI):

```bash
# Linux / macOS
HEADLESS=true mvn clean test

# Windows PowerShell
$env:HEADLESS="true"; mvn clean test
```

## Конфигурация

Параметры вынесены в `src/test/resources/configurations/config.properties`:

| Параметр | Назначение |
|---|---|
| `base.url`, `cart.url` | адреса магазина и корзины |
| `browser.chrome` | браузер (сейчас поддерживается Chrome) |
| `screen.width.resolution`, `screen.height.resolution` | размер окна браузера |
| `explicit.wait.time` | явное ожидание, сек |
| `sort.products.*` | ожидаемые названия вариантов сортировки |
| `min.products.count` | минимум товаров в категории |
| `amount.products.add.cart` | количество товаров в тесте корзины |
| `min.products.quantity`, `max.products.quantity` | диапазон случайного количества |

## CI

Workflow `.github/workflows/ui-tests.yml` запускается на push и pull request: поднимает JDK 17, прогоняет `mvn clean test` в headless-режиме, генерирует Allure-отчёт и сохраняет артефакты `allure-report` и `test-results`.

## Особенности

- Тесты зависят от внешнего сайта, поэтому возможны падения при изменении разметки или ассортимента.
- Скриншот прикрепляется к отчёту только при падении теста.
