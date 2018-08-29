package com.grumpyshoe.module.locationmanager

import android.app.Activity
import android.content.Intent
import android.location.Location
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
     */
    fun getLastKnownPosition(activity: Activity, onLastLocationFound: ((Location) -> Unit)?, onNoLocationFound: (() -> Unit)?)


    /**
     * handle permission request result
     *
     */
    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray): Boolean?


    /**
     * start location change tracker
     *
     */
    fun startLocationTracker(activity: Activity, config: LocationTrackerConfig, onLocationChange: (Location) -> Unit)


    /**
     * stop location change tracker
     *
     */
    fun stopLocationTracker()


    /**
     * handle onActivityResult for location settings resolver
     *
     */
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean?

}