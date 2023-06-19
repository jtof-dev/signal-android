# Signal-Android Full Installation Guide

## Useful Resources

- [Signal-Server installation instructions](https://github.com/JJTofflemire/Signal-Server)

## Dependancies

- Android Studio
  - If you are on Linux and Android Studio is buggy, try the [flatpak](https://flathub.org/apps/com.google.AndroidStudio)

## Installation

1. `git clone https://github.com/JJTofflemire/Signal-Android`

2. Open Android Studio and hit `Open` or in the top left: `File` > `Open Folder` > select `Signal-Android`

3. Let Gradle sync

4. Set up an Android emulator

    4.1. Either under `Tools` > `Device Manager` or the small phone icon near the top right of the screen. Hit `Create Device`

    4.2. Select any new-ish phone (Pixel 4xl for example)

    4.3. Select a new-ish system image - one might get automatically highlighted, if so install that one - just avoid the latest one (currently `API 34`)

## Configuration

5. All configuration that is needed is done in `app/build.gradle`. Currently it is renamed to `sample-build.gradle`, and it has Signal-Android's default config for connecting to Signal's server

6. Rename and configure the `app/build.gradle` (also located in the `Gradle Scripts` dropdown in Android Studio, with `(Module: Signal Android)` next the the build.gradle)

    6.1. Change lines 181 and 182 to:

`181      buildConfigField "String", "SIGNAL_URL", "\"your-domin\""`

`182      buildConfigField "String", "STORAGE_URL", "\"file:///path/to/storage""`

    6.2. You can use localhost for `SIGNAL_URL` and a local directory for `SIGNAL_STORAGE`

## Running

7. Start `Signal-Server`

8. Select `Signal-Android` and an emulator from the dropdown near the top right and hit the green run button

## To-Do

- ~~Get Android Studio to run without basically causing a memory leak - the problem was using the AUR to install Android Studio~~

- ~~Get the Android app to run (without any configuration yet)~~

- Figure out how the `certificate` modifier from Signal-Server ties into the Android app

- Set all URL paths to lead to localhost and get it to connect to a running Signal-Server instance inside Android Studio

  - Once localhost works, all configuration on transitioning to a website should be done on Signal-Server and just the URLs would need to be updated

- Get two physical Android phones to talk to each other using a private Signal-Server instance