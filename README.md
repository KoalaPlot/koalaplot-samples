# Koala Plot Samples

This repository houses samples demonstrating the use and features of Koala Plot libraries.

Try out
the [web version of the samples](https://koalaplot.github.io/koalaplot-samples/productionExecutable/index.html)
for a quick look at
the possibilities when using Koala Plot.

# How to run with a development build of koalaplot-core

1. Build koalaplot-core with the following commands

```shell
git clone https://github.com/KoalaPlot/koalaplot-core.git
cd koalaplot-core
./gradlew publishToMavenLocal
```

2. Build koalaplot-samples

```shell
cd ..
git clone https://github.com/KoalaPlot/koalaplot-samples.git
cd koalaplot-samples
./gradlew build
```

3. Run it
    1. For desktop:
   ```shell
   ./gradlew run
   ```
    2. For web:
   ```shell
    ./gradlew jsBrowserRun
    ```
    3. For Android, to run in an emulator on your machine:
        1. Download [Android Studio](https://developer.android.com/studio/)
        2. Open the koalaplot-samples folder as a project in Android Studio
        3. Run kaoalaplot-samples in the emulator. Further instructions on using the
           emulator [are available online](https://developer.android.com/studio/run/emulator).
    4. For iOS, to run in a simulator on your machine (only for macOS):
        1. Download [Xcode](https://apps.apple.com/ru/app/xcode/id497799835?mt=12)
        2. Open the iosApp folder as a project in Xcode
        3. Run kaoalaplot-samples in the simulator. Further instructions on using the
           simulator [are available online](https://developer.apple.com/documentation/xcode/running-your-app-in-simulator-or-on-a-device)
        4. You can also [launch the iOS app in Android Studio](https://www.jetbrains.com/help/kotlin-multiplatform-dev/multiplatform-create-first-app.html#run-your-application-on-ios) 
   
