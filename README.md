# Koala Plot Samples

This repository houses samples demonstrating the use and features of Koala Plot libraries.

Try out the [web version of the samples](https://koalaplot.github.io/koalaplot-samples/index.html) for a quick look at
the possibilities when using Koala Plot. Note that this uses the alpha Compose web-canvas capability, so there may be
bugs in the underlying Compose framework.

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
    3. For Android, to run in an emulator on your mahcine:
        1. Download [Android Studio](https://developer.android.com/studio/)
        2. Open the koalaplot-samples folder as a project in Android Studio
        3. Run kaoalaplot-samples in the emulator. Further instructions on using the
           emulator [are available online](https://developer.android.com/studio/run/emulator).
   
