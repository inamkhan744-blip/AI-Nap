# Android Manifest Configuration

## Overview

This document describes the Android manifest configuration for AI-Nap application.

## Required Permissions

### Location Services
```xml
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
```
- Used for location-based sleep insights
- Requested at runtime on Android 6.0+

### Sensor Access
```xml
<uses-permission android:name="android.permission.BODY_SENSORS" />
```
- Accesses accelerometer and gyroscope data
- Used for sleep movement detection
- Requested at runtime on Android 6.0+

### Health Data
```xml
<uses-permission android:name="com.google.android.gms.permission.ACTIVITY_RECOGNITION" />
```
- Accesses device activity recognition
- Used for sleep detection
- Requested at runtime

### Network
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```
- Required for API communication
- Cloud sync functionality
- Used to check network connectivity

### Storage
```xml
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```
- For exporting sleep data
- Requested at runtime on Android 6.0+

### Device Management
```xml
<uses-permission android:name="android.permission.WAKE_LOCK" />
```
- Prevents device from sleeping during active tracking
- Required for background services

### Notifications
```xml
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```
- Required for push notifications
- Requested at runtime on Android 13+

### Background Execution
```xml
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
```
- Restarts background services after device reboot

## Activities

### MainActivity
- Entry point for the application
- Displays main sleep tracking interface
- Action: `android.intent.action.MAIN`
- Category: `android.intent.category.LAUNCHER`

### SleepTrackingActivity
- Manages active sleep tracking session
- Displays real-time sleep metrics

### AnalyticsActivity
- Shows sleep statistics and history
- Displays AI recommendations

### SettingsActivity
- User preferences and configuration
- API key management
- Permission settings

## Services

### SleepTrackingService
- Background service for sleep tracking
- Runs continuously during sleep sessions
- Accesses sensors and location

### SyncService
- Cloud data synchronization
- Periodic background sync
- Handles offline data queuing

### NotificationService
- Manages push notifications
- Sends sleep recommendations
- Wakes user for optimal nap times

## Broadcast Receivers

### BootCompletedReceiver
- Listens for `BOOT_COMPLETED` intent
- Restarts background services

### LocationUpdateReceiver
- Receives location updates
- Triggers location-based analytics

## Content Providers

### SleepDataProvider
- Provides access to local sleep database
- Used by other apps for health integration
- Authority: `com.ai_nap.sleepdata`

## Intents

### Supported Custom Intents

#### Start Sleep Tracking
```
com.ai_nap.START_SLEEP_TRACKING
```
Starts a new sleep tracking session.

#### Stop Sleep Tracking
```
com.ai_nap.STOP_SLEEP_TRACKING
```
Stops the current tracking session.

#### View Analytics
```
com.ai_nap.VIEW_ANALYTICS
```
Opens analytics dashboard.

## Configuration

### Supported Screen Sizes
- Small phones (3.5" - 4.5")
- Normal phones (4.5" - 7")
- Large tablets (7" - 10")
- Extra large tablets (10"+)

### Supported Densities
- LDPI (120 dpi)
- MDPI (160 dpi)
- HDPI (240 dpi)
- XHDPI (320 dpi)
- XXHDPI (480 dpi)
- XXXHDPI (640 dpi)

### Orientation Support
- Portrait (primary)
- Landscape (secondary)
- Reverse Portrait
- Reverse Landscape

### Minimum SDK
- API Level 24 (Android 7.0)

### Target SDK
- API Level 34 (Android 14)

## Installation

### Default Installation Location
- Internal device storage
- Installable on external storage (SD card) if enabled

## Hardware Features

### Optional Features
```xml
<uses-feature android:name="android.hardware.sensor.accelerometer" android:required="false" />
<uses-feature android:name="android.hardware.sensor.gyroscope" android:required="false" />
<uses-feature android:name="android.hardware.location.gps" android:required="false" />
```

### Required Features
```xml
<uses-feature android:name="android.hardware.screen.portrait" />
```

## Updates and Maintenance

This manifest is maintained to ensure:
- Compliance with Android security best practices
- Support for latest Android versions
- Proper permission handling
- User privacy protection

For more information about Android Manifest, see:
[Android Manifest Documentation](https://developer.android.com/guide/topics/manifest/manifest-intro)
