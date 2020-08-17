package com.grumpyshoe.module.locationmanager.impl

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.grumpyshoe.locationmanager.R
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

    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var locationRequest: LocationRequest? = null
    private var lastTrackedLocation: Location? = null
    private var locationCallback: LocationCallback? = null
    private val permissionManager: PermissionManager = PermissionManagerImpl
    private val locationLiveData = MutableLiveData<Location?>()
    private lateinit var sharedPreferences: SharedPreferences
    private var activity: Activity? = null

    private fun initLocationManager(activity: Activity) {

        this.activity = activity

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)

        if (fusedLocationClient == null) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
        }
    }

    private fun checkIfLocationProviderIsEnabled(requestCode: Int): Boolean {
        try {
            val lm = activity?.getSystemService(Context.LOCATION_SERVICE) as android.location.LocationManager;
            var gps_enabled = false;
            var network_enabled = false;

            gps_enabled = lm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
            network_enabled = lm.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER);


            if (!gps_enabled && !network_enabled) {

                activity?.let {
                    AlertDialog.Builder(it)
                        .setMessage(R.string.locationmanager_gps_network_not_enabled)
                        .setPositiveButton(R.string.locationmanager_open_location_settings) { _, _ ->
                            run {
                                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).apply {
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                    addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                                }
                                it.startActivityForResult(intent, requestCode)
                            }
                        }
                        .setNegativeButton(R.string.locationmanager_btn_cancel, null)
                        .show();
                }
            } else {
                return true
            }
        } catch (ex: Exception) {
            Log.d(javaClass.simpleName, ex.message, ex)
        }
        return false
    }

    /**
     * handle permission request result
     *
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray): Boolean? {
        return permissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    /**
     * get last known location
     *
     */
    override fun getLastKnownLocation(activity: Activity): LiveData<Location?> {

        initLocationManager(activity)

        permissionManager.checkPermissions(
            activity = activity,
            permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
            onPermissionResult = { permissionResult ->
                if (permissionResult.areAllGranted()) {

                    if (checkIfLocationProviderIsEnabled(REQUEST_CHECK_SETTINGS_FOR_LAST_LOCATION)) {
                        requestLastKnownLocation(activity)
                    }
                }
            },
            requestCode = REQUEST_PERMISSION_LAST_KNOWN_LOCATION
        )

        return locationLiveData
    }

    /**
     * start location change tracker
     *
     */
    override fun startLocationTracker(activity: Activity, config: LocationTrackerConfig): LiveData<Location?> {

        initLocationManager(activity)

        locationRequest = LocationRequest().apply {
            interval = config.interval
            fastestInterval = config.fastestInterval
            priority = config.priority
        }

        permissionManager.checkPermissions(
            activity = activity,
            permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
            onPermissionResult = { permissionResult ->
                if (permissionResult.areAllGranted()) {
                    if (checkIfLocationProviderIsEnabled(REQUEST_CHECK_SETTINGS_FOR_LOCATION_TRACKER)) {
                        requestLocationUpdates()
                    }
                }
            },
            requestCode = REQUEST_PERMISSION_FINE_LOCATION_FOR_LOCATION_TRACKER
        )
        return locationLiveData
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
     * execute location change updates
     *
     */
    @SuppressLint("MissingPermission")
    private fun requestLastKnownLocation(activity: Activity?) {

        // post last known location
        fusedLocationClient
            ?.lastLocation
            ?.addOnSuccessListener { location: Location? ->
                locationLiveData.postValue(location)
            } ?: locationLiveData.postValue(null)
    }

    /**
     * execute location change updates
     *
     */
    @SuppressLint("MissingPermission")
    private fun requestLocationUpdates() {

        // post last known location
        fusedLocationClient
            ?.lastLocation
            ?.addOnSuccessListener { location: Location? ->
                locationLiveData.postValue(location)
            }

        // init callback
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    if (location.latitude != lastTrackedLocation?.latitude
                        || location.longitude != lastTrackedLocation?.longitude
                        || location.accuracy != lastTrackedLocation?.accuracy
                    ) {
                        locationLiveData.postValue(location)
                        lastTrackedLocation = location
                    }
                }
            }
        }

        // start location updates
        fusedLocationClient?.requestLocationUpdates(locationRequest, locationCallback, null)
    }


    /**
     * handle onActivityResult for location settings resolver
     *
     */
    @SuppressLint("ApplySharedPref")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean? {
        return if (requestCode == REQUEST_CHECK_SETTINGS_FOR_LAST_LOCATION && resultCode == PackageManager.PERMISSION_GRANTED) {
            requestLastKnownLocation(this.activity)
            true
        } else if (requestCode == REQUEST_CHECK_SETTINGS_FOR_LOCATION_TRACKER && resultCode == PackageManager.PERMISSION_GRANTED) {
            requestLocationUpdates()
            true
        } else {
            false
        }
    }

    companion object {
        private const val REQUEST_PERMISSION_LAST_KNOWN_LOCATION = 123
        private const val REQUEST_PERMISSION_FINE_LOCATION_FOR_LOCATION_TRACKER = 345

        private const val REQUEST_CHECK_SETTINGS_FOR_LOCATION_TRACKER = 234
        private const val REQUEST_CHECK_SETTINGS_FOR_LAST_LOCATION = 549
    }
}