<p align="center">
 <a href="../../releases/latest">
 <img src="./app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png" height="60">
  </a>
</p>

# project-three_musketeers
project-three_musketeers created by GitHub Classroom

# Тема проекта - мобильное приложение. 
Создание мобильного приложения, минимум - для отслеживания состояния человека (в дороге, дома, на работе), максимум - возможность отслеживания произвольных поэтапных процессов (минимум - один из шаблонов).

# Как запустить приложение:
( В данном варианте нет подключения к БД разработчиков, вам нужно будет самим создать свою БД и добавить файл google-services.json в проект )
#### 1 вариант:
 1. Установить:
    * Java ( https://www.java.com/ )  
    * Android - SDK ( https://developer.android.google.cn/studio/releases/platform-tools )
    * Gradle ( https://gradle.org/install/ ) 
 2. Склонировать репозиторий: `git clone https://github.com/TRPP-IKBO-06/project-three_musketeers.git`
 3. Открыть консоль в директории проекта ( project-three_musketeers ): `gradlew build` ( Для Win можете использовать gradlew.bat ) 
 4. Перейти в `project-three_musketeers/app/build/outputs/apk/releas` и ввести команду: `keytool -genkey -v -keystore stager-release-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias stager-key`
 5. Ввести команду:
    * Win: {Абсолютный путь к sdk, пример: D:\Data\sdk\build-tools\30.0.3\}`zipalign -v -p 4 app-release-unsigned.apk stager-aligned.apk`
    * Linux: `zipalign -v -p 4 app-release-unsigned.apk stager-aligned.apk`
 6. Ввести команду:
    * Win: {Абсолютный путь к sdk}`apksigner sign --ks stager-release-key.jks --out app-release-unsigned.apk stager-aligned.apk`
    * Linux: `apksigner sign --ks stager-release-key.jks --out app-release-unsigned.apk stager-aligned.apk`
 7. В папке `app/build/outputs/apk/release` вы найдете `app-release-unsigned.apk`
 8. Перекидываем на устройство, распаковать (у вас должна быть разрешена установка из неизвестных источников)!

#### 2 вариант(через Docker):
 1. Скачать Docker Desktop ( https://www.docker.com/products/docker-desktop )
 2. Cклонировать репозиторий: `git clone https://github.com/TRPP-IKBO-06/project-three_musketeers.git` 
 3. Перейти в папку `project-three_musketeers`
 4. Прописать следующую команду: `docker pull dedicated407/androidstager`
 5. Прописать следующую команду: 
    * Для Win: `docker run --rm -v "%cd%":/home/gradle/ dedicated407/androidstager sh /home/makeapk`
    * Для Linux: `docker run --rm -v "$PWD":/home/gradle/ dedicated407/androidstager sh /home/makeapk`
 6. После работы контейнера в вашей папке появится папка `release`, в ней содержится `stager.apk`, файл с приложением

## Информация по сдаче итогового проекта
### 1. Информация про проект, UserStory, Макет
  * Дата сдачи: 4 марта 2021г
  * Баллы: 2 балла

### 2. Работа над проектом. Оформление ReadMe
  * Дата сдачи: 29 апреля 2021г
  * Баллы: 1 балл

### 3. Docker
  * Дата сдачи: 29 апреля 2021г
  * Баллы: 2 балла
