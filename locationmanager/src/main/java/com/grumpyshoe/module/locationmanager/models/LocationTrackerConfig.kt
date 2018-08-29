package com.grumpyshoe.module.locationmanager.models

import com.google.android.gms.location.LocationRequest

/**
 * <p>LocationTrackerConfig is a wrapper for all information fpr the tracker.</p>
 *
 * @since    1.0.0
 * @version  1.0.0
 * @author   grumpyshoe
 *
 */
data class LocationTrackerConfig(val interval: Long = 10000, val fastestInterval: Long = 5000L, val priority: Int = LocationRequest.PRIORITY_HIGH_ACCURACY)