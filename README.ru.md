# Менеджер Здоровья Глаз
[English](https://github.com/RznNike/EyeHealthManager#readme) | [Русский](/README.ru.md)

<img src="/readme_files/icon.png" alt="icon" width="200"/>

Приложение для контроля здоровья ваших глаз.
## Загрузки
Вы можете установить это приложение из магазина:

[<img src="/readme_files/ru/badge_google_play.png" alt="Скачать из Google Play" height="60"/>](https://play.google.com/store/apps/details?id=ru.rznnike.eyehealthmanager)

Или вы можете скачать apk со [станицы releases](https://github.com/RznNike/EyeHealthManager/releases).
## Описание
Контролируйте здоровье ваших глаз с помощью смартфона!

Приложение Менеджер Здоровья Глаз предназначено для периодического контроля состояния ваших глаз. С ним вы можете не только проверить ваше зрение, но также вести журнал проведенных тестов и анализировать полученные результаты.

В приложении реализованы различные виды тестов:
* тест остроты зрения;
* тест на астигматизм;
* тест на близорукость и дальнозоркость;
* тест цветовосприятия;
* тест на дальтонизм;
* тест контрастности зрения.

Помимо этого также доступны другие полезные функции:
* журнал проведенных тестов;
* анализ собранных данных (для теста остроты зрения);
* импорт и экспорт журнала в файл;
* настройка правильного масштабирования изображения во время тестов;
* поддержка русского и английского языков.
## Скриншоты
<img src="/readme_files/ru/screenshot_1.png" alt="icon" width="250"/> <img src="/readme_files/ru/screenshot_2.png" alt="icon" width="250"/> <img src="/readme_files/ru/screenshot_3.png" alt="icon" width="250"/>

<details>
    <summary>Больше скриншотов</summary>
    <img src="/readme_files/ru/screenshot_4.png" alt="icon" width="250"/>
    <img src="/readme_files/ru/screenshot_5.png" alt="icon" width="250"/>
    <img src="/readme_files/ru/screenshot_6.png" alt="icon" width="250"/>
    <img src="/readme_files/ru/screenshot_7.png" alt="icon" width="250"/>
    <img src="/readme_files/ru/screenshot_8.png" alt="icon" width="250"/>
</details>

## Архитектура проекта
Этот проект использует:
* [Kotlin](https://kotlinlang.org/)
* [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
* [Single Activity](https://www.toolify.ai/ai-news/mastering-single-activity-in-android-development-176852)
* [Moxy](https://github.com/moxy-community/Moxy) (MVP)
* [Koin](https://github.com/InsertKoinIO/koin) (DI)
* [Cicerone](https://github.com/terrakok/Cicerone) (навигация)
* [FastAdapter](https://github.com/mikepenz/FastAdapter) (списки)
* [Coil](https://github.com/coil-kt/coil) (загрузка изображений)
* [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart) (графики)
* [ViewBindingPropertyDelegate](https://github.com/kirich1409/ViewBindingPropertyDelegate) (view binding)
* [ObjectBox](https://github.com/objectbox/objectbox-java) (база данных)