
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
  implementation 'com.github.grumpyshoe:android-module-locationmanager:1.2.0'
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

 Also it's necessary to delegate the response of 'ònActivityResult`to your LocationManager instance
   ```
   override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
           locationManager.onActivityResult(requestCode, resultCode, data)
               ?: super.onActivityResult(requestCode, resultCode, data)
   }
   ```

  ### Get last known Location
  Try to get last known connection with `getLastKnownLocation`.
  ```
  locationManager.getLastKnownLocation(activity = this).observe(
        this,
        Observer<Location?> { location ->
            // handle location data
        })
  ```


  ### Track location
  If you want continuous information about device movement start the location tracking.

  #### Start location tracking
  To start location tracking call `startLocationTracker`.
  ```
  locationManager.startLocationTracker(
      activity = this, config = LocationTrackerConfig()
  ).observe(
      this,
      Observer<Location?> { location ->
          // handle location data
      })
  ```

  #### Stop location tracking
  To stop location tracking call `stopLocationTracker`. This should be done at LifeCycleEvent `onDestroy` at least to avoid any error during off screen information.
  ```
  locationManager.stopLocationTracker()

  ```

  ### Customization
  If the Permission is granted but the location provider is disabled a AlertDialog is shown. 
  The options are 'Cancel' and 'Settings' to enable the Location Service again.
  
  For customizing the text that is shown at the AlertDialog the following string resources must be overridden at you app:
   - `R.string.locationmanager_gps_network_not_enabled` (default: "Location Provider is not enabled. This Service is required otherwise no results will be posted.")
   - `R.string.locationmanager_open_location_settings` (default: "Settings")
   - `R.string.locationmanager_btn_cancel` (default: "Cancel")
  

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
    Android Studio 4.0.1
    Build #AI-193.6911.18.40.6626763, built on June 25, 2020
    Runtime version: 1.8.0_242-release-1644-b3-6222593 x86_64
    VM: OpenJDK 64-Bit Server VM by JetBrains s.r.o
    macOS 10.15.5
  ```
