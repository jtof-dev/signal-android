# Signal-Android Full Installation Guide

- An older fork of [Signal-Android](https://github.com/signalapp/Signal-Android) that works with [my fork of Signal-Server](https://github.com/JJTofflemire/Signal-Server) 
  - Running the newest version of signalapp's Android app with my fork of Signal-Server should be fine, but this repository is here as a redundancy

## Useful Resources

- [Signal-Server installation instructions](https://github.com/JJTofflemire/Signal-Server)
- [sample-build.gradle](app/sample-build.gradle) (with notes)

## Dependencies

- Android Studio

  - If you are on Linux and Android Studio is buggy, try the [flatpak](https://flathub.org/apps/com.google.AndroidStudio)

## Compilation

Clone this repo with:

```
git clone https://github.com/JJTofflemire/Signal-Android
```

Open Android Studio and hit `Open` in the new project prompt or in the top left go to `File` > `Open Folder` > select `Signal-Android`

Let Gradle sync

Set up an Android emulator

- Either under `Tools` > `Device Manager` or the small phone icon near the top right of the screen. Hit `Create Device`

- Select any new-ish phone (Pixel 4XL for example)

- Select a new-ish system image - one might get automatically highlighted, if so install that one - just avoid the latest one (currently `API 34`)

## Configuration

[This guide](https://github.com/madeindra/signal-setup-guide/tree/master/signal-android) by Madeindra is still almost entierly up-to-date and I will be adapting basically word-for-word from here

All configuration that is needed is done in [`app/build.gradle`](app/sample-build.gradle). Currently it is renamed to `sample-build.gradle`, and it has Signal-Android's default config for connecting to Signal's server

- Starting on line 181, follow the comments on changing URL's

Set up firebase

- In `app/src/main/res/raw`, you need to add your server's certificates. I renamed `whisper.store` to `sample-whisper.store`

## Starting the app

Start `Signal-Server`

Select `Signal-Android` and an emulator from the dropdown near the top right and hit the green run button

## To-Do

### Configuring the app: