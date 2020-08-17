package com.grumpyshoe.module.locationmanager

import android.app.Activity
import android.content.Intent
import android.location.Location
import androidx.lifecycle.LiveData
import com.grumpyshoe.module.locationmanager.models.LocationTrackerConfig


/**
 * <p>LocationManager - interface for easy access to location handling</p>
 *
 * @since    1.0.0
 * @version  1.0.0
 * @author   grumpyshoe
 *
 */
interface LocationManager {

    /**
     * get last known location
     *
     * this method only updates once it' value
     */
    fun getLastKnownLocation(activity: Activity, checkProviderEnabled: Boolean = true): LiveData<Location?>

    /**
     * start location tracker
     *
     */
    fun startLocationTracker(activity: Activity, config: LocationTrackerConfig = LocationTrackerConfig(), checkProviderEnabled: Boolean = true): LiveData<Location?>

    /**
     * stop location tracker
     *
     */
    fun stopLocationTracker()

    /**
     * handle permission request result
     *
     */
    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray): Boolean?

    /**
     * handle onActivityResult for location settings resolver
     *
     */
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean?

}