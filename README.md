# Android Activity Tracker SDK

## Description
This is an SDK for Android that tracks user's activity within an app. It provides easy integration and a range of features to help you understand user behavior and engagement.

## Requirements
- **Minimum API Level**: The SDK requires Android API level 24 (Android 7.0, Nougat) or higher.
- **Network Connection**: The app needs a network connection to fully utilize this SDK.

## Building the SDK
To build the SDK as an `aar` file, follow these steps:

1. Open the project in Android Studio.
2. Navigate to the module you want to build.
3. From the top menu, select `Build > Build Bundle(s) / APK(s) > Build APK(s)`.
4. Once the build completes, the `aar` file will be located in `activity-tracker-sdk/build/outputs/aar/`.

Or use terminal at root project
```bash
./gradlew :activity-tracker-sdk:clean
./gradlew :activity-tracker-sdk:assemble
```

## Integration
### Adding Dependencies
Include the following dependencies in your app's `build.gradle` file:

```DSL
dependencies {
    implementation(files("activity-tracker-sdk-release-file-path.aar"))
    // or implementation 'com.example:activity-tracker-sdk:latestVersion' // Replace with the latest version
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-gson:$retrofitVersion")
    implementation("com.squareup.okhttp3:logging-interceptor:$loggingVersion")
    implementation("com.google.code.gson:gson:$gsonVersion")
}
```

### Setting Android Permissions
Add the following permissions to your AndroidManifest.xml:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

These permissions are necessary for network communication and tracking activity states.

## Usage
You can see example app at app module at this project. Or another example to explore integration.

```kotlin
ActivityTrackerManager.initialize(options)
ActivityTrackerManager.sendStartLevelEvent(1, "beginner")
ActivityTrackerManager.sendEndLevelEvent(1, "beginner", true)
ActivityTrackerManager.sendCustomEvent("DummyEvent", mapOf("key" to "Hello world").toJsonString())
ActivityTrackerManager.shutdown()
```

### Important Note
Do not forget to initialize the ActivityTrackerManager before making any SDK-related calls. Failing to do so will result in errors or unexpected behavior.
The applicationContext is used to avoid memory leaks. Always pass the application context, not an activity context, to the SDK initializer.
## Support
For support, please contact <khoapham.wrk@gmail.com>.

## License

## Additional Information
Add any other information that users of your SDK might find helpful.
