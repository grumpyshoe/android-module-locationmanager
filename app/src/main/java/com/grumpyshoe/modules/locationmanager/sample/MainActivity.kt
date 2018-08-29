package com.grumpyshoe.modules.locationmanager.sample

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.grumpyshoe.module.locationmanager.LocationManager
import com.grumpyshoe.module.locationmanager.impl.LocationManagerImpl
import com.grumpyshoe.module.locationmanager.models.LocationTrackerConfig
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    // get instance of location manager
    val locationManager: LocationManager = LocationManagerImpl()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // get last location
        btn_last_location.setOnClickListener {
            locationManager.getLastKnownPosition(
                    activity = this,
                    onLastLocationFound = {
                        Log.d("Location", "Last Location - found - lat: " + it.latitude + " lng:" + it.longitude)
                        result.text = "Last Location - found\nlat: " + it.latitude + "\nlng:" + it.longitude + "\naccuracy: " + it.accuracy
                    },
                    onNoLocationFound = {
                        Log.d("Location", "Last Location - no location found")
                        result.text = "Last Location - no location found"
                    })
        }


        // start location tracking
        btn_tracker_start.setOnClickListener {
            locationManager.startLocationTracker(
                    activity = this,
                    onLocationChange = {
                        Log.d("Location", "New Location - found - lat: " + it.latitude + " lng:" + it.longitude)
                        result.text = "Location found\nlat:" + it.latitude + "\nlng:" + it.longitude + "\naccuracy: " + it.accuracy
                    },
                    config = LocationTrackerConfig())
        }

        btn_tracker_stop.setOnClickListener {
            locationManager.stopLocationTracker()
        }

    }


    /**
     * handle permission request result
     *
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        locationManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
                ?: super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }

}
