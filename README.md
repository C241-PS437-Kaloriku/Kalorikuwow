# Mobile-Development
Kaloriku is an application that provides food recommendations dan food recognation. This application is built using Kotlin programming language and Android Studio.

[![KpFedr.Kaloriku.jpeg](https://i.im.ge/2024/06/21/KpFedr.Kaloriku.jpeg)](https://im.ge/i/Kaloriku.KpFedr)

## Features

- Provides food recommendations based on user input
- Stores user preferences using Datastore
- Uses TensorFlow Lite for image recognition
- Integrates with CameraX for capturing images
- Uses Retrofit for network communication

## Requirements

- Android Studio
- Kotlin
- Minimum SDK: 24
- Target SDK: 34
- Internet connection

## Project Setup

Add your base URLs in the `buildConfigField` in the `build.gradle.kts` file:
```
buildConfigField("String", "BASE_URL", "\"YOUR_URL\"")
buildConfigField("String","PREDICT_BASE_URL","\"YOUR_URL\"")
```
## Installation
1. Clone this repository:
   ```
   git clone https://github.com/C241-PS437-Kaloriku/Mobile-Development.git
   ```
2. Open the project in Android Studio.

## Project Structure
- `app/src/main/java/com/dicoding/kaloriku/` - Application source code
- `app/src/main/res/`  - Application resources

## Dependencies
Here is the list of dependencies used in this project:
- [Tensorflow Lite](https://www.tensorflow.org/lite)
- [DataStore](https://developer.android.com/jetpack/androidx/releases/datastore)
- [RecycleView](https://developer.android.com/jetpack/androidx/releases/recyclerview)
- [Glide](https://github.com/bumptech/glide)
- [CameraX](https://developer.android.com/media/camera/camerax)
- [OkHttp](https://square.github.io/okhttp/)
- [Lifecycle](https://developer.android.com/jetpack/androidx/releases/lifecycle)

