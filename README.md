
  # Locationmanager

  ![AndroidStudio](https://img.shields.io/badge/Android_Studio-3.1.4-brightgreen.svg)
  ![minSDK](https://img.shields.io/badge/minSDK-API_16-orange.svg?style=flat)

  `Locationmanager` is a small wrapper for [Location Tracker](https://developer.android.com/training/location/).

  ## Installation

  - Add `jitpack`to your repositories in Project `build.gradle` :
  ```
  allprojects {
      repositories {
          ...
          maven { url "https://jitpack.io" }
      }
      ...
  }
  ```

  - Add dependency :
  ```
  implementation 'com.github.grumpyshoe:android-module-locationmanager:1.0.0'
  ```


  ## Usage

  - Get instance of LocationManager:
  ```
  val locationManager : LocationManager = LocationManagerImpl()
  ```

  In order to locate your device, the library will request the permissions
  `android.permission.ACCESS_FINE_LOCATION` and `android.permission.ACCESS_COARSE_LOCATION`.
  To handle to permission request result correctly delegate the response of `onRequestPermissionsResult` in your activity to the library like this:
  ```
  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
      locationManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
              ?: super.onRequestPermissionsResult(requestCode, permissions, grantResults)

  }
  ```

  ### Get last known Location
  Try to get last known connection with `getLastKnownPosition`.
  ```
  locationManager.getLastKnownPosition(
          activity = this,
          onLastLocationFound = { location ->
              // handle location data
          },
          onNoLocationFound = {
              // handle no location data
          })

  ```


  ### Track location
  If you want continuous information about device movement start the location tracking.

  #### Start location tracking
  To start location tracking call `startLocationTracker`.
  ```
  locationManager.startLocationTracker(
          activity = this ,
          onLocationChange = { location ->
              // handle location data
          },
          config = LocationTrackerConfig())

  ```

  #### Stop location tracking
  To stop location tracking call `stopLocationTracker`. This should be done at LifeCycleEvent `onDestroy` at least to avoid any error during off screen information.
  ```
  locationManager.stopLocationTracker()

  ```

  ### Dependencies
  | Package  | Version  |
  | ------------ | ------------ |
  | com.google.android.gms:play-services-location  | 15.0.1  |


  ## Need Help or something missing?

  Please [submit an issue](https://github.com/grumpyshoe/android-module-locationmanager/issues) on GitHub.


  ## License

  This project is licensed under the terms of the MIT license. See the [LICENSE](LICENSE) file.

  ## Build Environment
  ```
  Android Studio 3.1.4
  Build #AI-173.4907809, built on July 23, 2018
  JRE: 1.8.0_152-release-1024-b01 x86_64
  JVM: OpenJDK 64-Bit Server VM by JetBrains s.r.o
  Mac OS X 10.13.4
  ```
