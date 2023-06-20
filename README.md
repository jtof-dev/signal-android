# Signal-Android Full Installation Guide

- An older fork of [Signal-Android](https://github.com/signalapp/Signal-Android) that works with [my fork of Signal-Server](https://github.com/JJTofflemire/Signal-Server) 
  - Running the newest version of signalapp's Android app with my fork of Signal-Server should be fine, but this repository is here as a redundancy

## Useful Resources

- [Signal-Server installation instructions](https://github.com/JJTofflemire/Signal-Server)
- [sample-build.gradle](app/sample-build.gradle) (with notes)

## Dependancies

- Android Studio
  - If you are on Linux and Android Studio is buggy, try the [flatpak](https://flathub.org/apps/com.google.AndroidStudio)

## Installation

1. `git clone https://github.com/JJTofflemire/Signal-Android`

2. Open Android Studio and hit `Open` or in the top left: `File` > `Open Folder` > select `Signal-Android`

3. Let Gradle sync

4. Set up an Android emulator

    4.1. Either under `Tools` > `Device Manager` or the small phone icon near the top right of the screen. Hit `Create Device`

    4.2. Select any new-ish phone (Pixel 4XL for example)

    4.3. Select a new-ish system image - one might get automatically highlighted, if so install that one - just avoid the latest one (currently `API 34`)

## Configuration

5. All configuration that is needed is done in [`app/build.gradle`](app/sample-build.gradle). Currently it is renamed to `sample-build.gradle`, and it has Signal-Android's default config for connecting to Signal's server

6. Rename and configure the `app/build.gradle` (also located in the `Gradle Scripts` dropdown in Android Studio, with `(Module: Signal Android)` next the the build.gradle)

    6.1. Starting on line 181, follow the comments on changing URL's

7. I am unsure how much more needs to be done, however [aqnouch's guide](https://github.com/aqnouch/Signal-Setup-Guide/tree/master/signal-android) appears to still be relavent

    7.1. Presumably, after following all those steps, the app will connect to the server?

## Running

8. Start `Signal-Server`

9. Select `Signal-Android` and an emulator from the dropdown near the top right and hit the green run button

## To-Do

- ~~Get Android Studio to run without basically causing a memory leak - the problem was using the AUR to install Android Studio~~

- ~~Get the Android app to run (without any configuration yet)~~

- Follow aqnouch's guide on setting up the app

- Figure out how the `certificate` modifier from Signal-Server ties into the Android app

- Set all URL paths to lead to localhost and get it to connect to a running Signal-Server instance inside Android Studio

  - Once localhost works, all configuration on transitioning to a website should be done on Signal-Server and just the URLs would need to be updated

- Get two physical Android phones to talk to each other using a private Signal-Server instance