# Eye Health Manager
[English](https://github.com/RznNike/EyeHealthManager#readme) | [Русский](/README.ru.md)

<img src="/readme_files/icon.png" alt="icon" width="200"/>

An app to monitor your eye health.
## Downloads
You can install this app from the store:

[<img src="/readme_files/en/badge_google_play.png" alt="Get it on Google Play" height="60"/>](https://play.google.com/store/apps/details?id=ru.rznnike.eyehealthmanager) [<img src="/readme_files/badge_rustore.png" alt="Get it on RuStore" height="60"/>](https://apps.rustore.ru/app/ru.rznnike.eyehealthmanager)

Or you can download the apk from the [releases page](https://github.com/RznNike/EyeHealthManager/releases).
## Description
Monitor your eye health using your smartphone!

The Eye Health Manager application is designed to periodically monitor the condition of your eyes. With it, you can not only test your vision, but also keep a journal of the tests performed and analyze the results.

The application implements various types of tests:
* visual acuity test;
* astigmatism test;
* nearsightedness and farsightedness test;
* color perception test;
* color blindness test;
* vision contrast test.

In addition to this, other useful features are also available:
* journal of tests performed;
* analysis of collected data (for visual acuity test);
* import and export of the journal to a file;
* setting the correct image scaling during tests;
* support for Russian and English languages.
## Screenshots
<img src="/readme_files/en/screenshot_1.png" alt="icon" width="250"/> <img src="/readme_files/en/screenshot_2.png" alt="icon" width="250"/> <img src="/readme_files/en/screenshot_3.png" alt="icon" width="250"/>

<details>
    <summary>More screenshots</summary>
    <img src="/readme_files/en/screenshot_4.png" alt="icon" width="250"/>
    <img src="/readme_files/en/screenshot_5.png" alt="icon" width="250"/>
    <img src="/readme_files/en/screenshot_6.png" alt="icon" width="250"/>
    <img src="/readme_files/en/screenshot_7.png" alt="icon" width="250"/>
    <img src="/readme_files/en/screenshot_8.png" alt="icon" width="250"/>
</details>

## Project acrchitecture
This project uses:
* [Kotlin](https://kotlinlang.org/)
* [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
* [Single Activity](https://www.toolify.ai/ai-news/mastering-single-activity-in-android-development-176852)
* [Moxy](https://github.com/moxy-community/Moxy) (MVP)
* [Koin](https://github.com/InsertKoinIO/koin) (DI)
* [Cicerone](https://github.com/terrakok/Cicerone) (navigation)
* [FastAdapter](https://github.com/mikepenz/FastAdapter) (lists)
* [Coil](https://github.com/coil-kt/coil) (image loader)
* [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart) (charts)
* [ViewBindingPropertyDelegate](https://github.com/kirich1409/ViewBindingPropertyDelegate) (view binding)
* [ObjectBox](https://github.com/objectbox/objectbox-java) (database)