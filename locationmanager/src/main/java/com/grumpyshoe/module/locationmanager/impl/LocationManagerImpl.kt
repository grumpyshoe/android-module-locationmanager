package com.grumpyshoe.module.locationmanager.impl

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.widget.Toast
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.grumpyshoe.module.locationmanager.LocationManager
import com.grumpyshoe.module.locationmanager.models.LocationTrackerConfig
import com.grumpyshoe.module.permissionmanager.PermissionManager
import com.grumpyshoe.module.permissionmanager.impl.PermissionManagerImpl


/**
 * <p>LocationManagerImpl is based on LocationManager and contains all logic for handling location changes</p>
 *
 * @since    1.0.0
 * @version  1.0.0
 * @author   grumpyshoe
 *
 */
class LocationManagerImpl : LocationManager {

    private val REQUEST_PERMISSION_FINE_LOCATION_FOR_LAST_POSITION = 123
    private val REQUEST_PERMISSION_FINE_LOCATION_FOR_LOCATION_TRACKER = 345
    private val REQUEST_CHECK_SETTINGS_FOR_LOCATION_TRACKKER = 234
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var onLastLocationFound: ((Location) -> Unit)? = null
    private var onNoLocationFound: (() -> Unit)? = null
    private var onLocationChange: ((Location) -> Unit)? = null
    private var locationRequest: LocationRequest? = null
    private var lastTrackedLocation: Location? = null
    private var locationCallback: LocationCallback? = null
    private val permissionManager: PermissionManager = PermissionManagerImpl


    /**
     * get last known location
     *
     */
    @SuppressLint("MissingPermission")
    override fun getLastKnownPosition(activity: Activity, onLastLocationFound: ((Location) -> Unit)?, onNoLocationFound: (() -> Unit)?) {

        if (fusedLocationClient == null) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
        }

        this.onLastLocationFound = onLastLocationFound
        this.onNoLocationFound = onNoLocationFound

        permissionManager.checkPermissions(
                activity = activity,
                permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                onPermissionResult = { permissionResult ->
                    if(permissionResult.areAllGranted()) {
                        getLastLocation()
                    }
                },
                requestCode = REQUEST_PERMISSION_FINE_LOCATION_FOR_LAST_POSITION
        )

    }


    /**
     * handle permission request result
     *
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray): Boolean? {
        return permissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    /**
     * start location change tracker
     *
     */
    override fun startLocationTracker(activity: Activity, config: LocationTrackerConfig, onLocationChange: (Location) -> Unit) {

        this.onLocationChange = onLocationChange

        if (fusedLocationClient == null) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
        }

        locationRequest = LocationRequest().apply {
            interval = config.interval
            fastestInterval = config.fastestInterval
            priority = config.priority
        }

        val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest!!)

        val client: SettingsClient = LocationServices.getSettingsClient(activity)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener { locationSettingsResponse ->
            // All location settings are satisfied. The client can initialize
            // location requests here.
            // ...


            permissionManager.checkPermissions(
                    activity = activity,
                    permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                    onPermissionResult = { permissionResult ->
                        if(permissionResult.areAllGranted()) {
                            requestLocationUpdates()
                        }
                    },
                    requestCode = REQUEST_PERMISSION_FINE_LOCATION_FOR_LOCATION_TRACKER
            )
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    exception.startResolutionForResult(activity, REQUEST_CHECK_SETTINGS_FOR_LOCATION_TRACKKER)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }


    /**
     * stop location change tracker
     *
     */
    override fun stopLocationTracker() {
        locationCallback?.let {
            fusedLocationClient?.removeLocationUpdates(locationCallback)
        }
    }


    /**
     * execute las location request
     *
     */
    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        fusedLocationClient
                ?.lastLocation
                ?.addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        onLastLocationFound?.invoke(location)
                    } else {
                        onNoLocationFound?.invoke()
                    }
                } ?: onNoLocationFound?.invoke()
    }


//    /**
//     * check for required permissions
//     *
//     */
//    private fun checkPermissionIsGranted(activity: Activity, permissionList: Array<String>, requestCode: Int): Boolean? {
//
//        val notGrantedPermissionList = mutableListOf<String>()
//        permissionList.forEachIndexed { index, permission ->
//            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED){
//                notGrantedPermissionList.add(permission)
//            }
//        }
//
//        if (notGrantedPermissionList.isNotEmpty()) {
//
//            // Should we show an explanation?
//            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, notGrantedPermissionList.get(0))) {
//
//                Toast.makeText(activity.applicationContext, "Needed", Toast.LENGTH_SHORT).show()
//
//                ActivityCompat.requestPermissions(activity, notGrantedPermissionList.toTypedArray(), requestCode)
//
//                // Show an explanation to the user *asynchronously* -- don't block
//                // this thread waiting for the user's response! After the user
//                // sees the explanation, try again to request the permission.
//            } else {
//                // No explanation needed, we can request the permission.
//                ActivityCompat.requestPermissions(activity, permissionList, requestCode)
//
//                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
//                // app-defined int constant. The callback method gets the
//                // result of the request.
//            }
//
//            return null
//        } else {
//            // Permission has already been granted
//            if (requestCode == REQUEST_PERMISSION_FINE_LOCATION_FOR_LAST_POSITION) {
//                getLastLocation()
//            } else if (requestCode == REQUEST_PERMISSION_FINE_LOCATION_FOR_LOCATION_TRACKER) {
//                requestLocationUpdates()
//            }
//            return true
//
//        }
//
//    }


    /**
     * execute location change updates
     *
     */
    @SuppressLint("MissingPermission")
    private fun requestLocationUpdates() {

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    if (location.latitude != lastTrackedLocation?.latitude
                            || location.longitude != lastTrackedLocation?.longitude
                            || location.accuracy != lastTrackedLocation?.accuracy) {
                        onLocationChange?.invoke(location)
                        lastTrackedLocation = location
                    }
                }
            }
        }


        fusedLocationClient?.requestLocationUpdates(locationRequest, locationCallback, null /* Looper */)

    }


    /**
     * handle onActivityResult for location settings resolver
     *
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean? {
        if (requestCode == REQUEST_CHECK_SETTINGS_FOR_LOCATION_TRACKKER) {
            requestLocationUpdates()
            return true
        }
        return false
    }
}