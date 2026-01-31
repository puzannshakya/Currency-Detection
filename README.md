# Currency Detection

An Android application for real-time currency detection and recognition using on-device machine learning.

## Overview

This application performs currency detection directly on mobile devices. The training process is executed on-device, with extracted keypoints stored in a local SQLite database for fast recognition.

## Features

- **On-Device Training**: Train the model directly on your Android device
- **Local Database Storage**: Keypoints are saved to SQLite for quick access
- **Real-Time Detection**: Recognize currency in real-time using your device camera
- **Offline Operation**: No internet connection required after training

## Setup Instructions

### For Training Mode

To enable training mode, you need to modify the `AndroidManifest.xml` file to launch the training activity directly.

**Change the following:**
```xml
<activity
    android:name=".SplashActivity"
    android:theme="@style/AppTheme.NoActionBar">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>
<activity 
    android:name=".ReaderMainActivity"
    android:theme="@style/AppTheme.NoActionBar"/>
<activity android:name=".KeypointsActivity">
</activity>
```

**To this:**
```xml
<activity
    android:name=".SplashActivity"
    android:theme="@style/AppTheme.NoActionBar">
</activity>
<activity 
    android:name=".ReaderMainActivity"
    android:theme="@style/AppTheme.NoActionBar"/>
<activity android:name=".KeypointsActivity">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>
```

### After Training is Complete

Once you've finished training the model, switch back to normal operation mode by reverting the `AndroidManifest.xml` changes:

**Change back to:**
```xml
<activity
    android:name=".SplashActivity"
    android:theme="@style/AppTheme.NoActionBar">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>
<activity 
    android:name=".ReaderMainActivity"
    android:theme="@style/AppTheme.NoActionBar"/>
<activity android:name=".KeypointsActivity">
</activity>
```

## How It Works

1. **Training Phase**: Launch the app in training mode to capture and process currency images, extracting keypoints that are saved to the local database
2. **Detection Phase**: Switch to normal mode to use the trained model for real-time currency recognition

## Requirements

- Android device with camera
- Minimum Android SDK version: [specify your minimum SDK]
- Storage permission for database operations

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

