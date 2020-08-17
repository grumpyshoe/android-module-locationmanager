package com.grumpyshoe.modules.locationmanager.sample

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.grumpyshoe.module.locationmanager.LocationManager
import com.grumpyshoe.module.locationmanager.impl.LocationManagerImpl
import com.grumpyshoe.module.locationmanager.models.LocationTrackerConfig
import kotlinx.android.synthetic.main.activity_main.btn_clear_result
import kotlinx.android.synthetic.main.activity_main.btn_last_location
import kotlinx.android.synthetic.main.activity_main.btn_tracker_start
import kotlinx.android.synthetic.main.activity_main.btn_tracker_stop
import kotlinx.android.synthetic.main.activity_main.result

class MainActivity : AppCompatActivity() {

    // get instance of location manager
    private val locationManager: LocationManager = LocationManagerImpl()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val locationObserver = Observer<Location?> { location ->
            location?.let {
                Log.d("Location", "Location - found - lat: ${it.latitude} lng: ${it.longitude}")
                result.text = "Last Location - found\nlat: ${it.latitude}\nlng:${it.longitude}\naccuracy: ${it.accuracy}"
            } ?: run {
                Log.d("Location", "Location - no location found")
                result.text = "Last Location - no location found"
            }
        }

        // get last known location
        btn_last_location.setOnClickListener {
            locationManager.getLastKnownLocation(activity = this).observe(
                this,
                Observer<Location?> { location ->
                    location?.let {
                        Log.d("Location", "Location - found - lat: ${it.latitude} lng: ${it.longitude}")
                        result.text = "Last Location - found\nlat: ${it.latitude}\nlng:${it.longitude}\naccuracy: ${it.accuracy}"
                    } ?: run {
                        Log.d("Location", "Location - no location found")
                        result.text = "Last Location - no location found"
                    }
                })
        }

        // start location tracking
        btn_tracker_start.setOnClickListener {
            locationManager.startLocationTracker(
                activity = this, config = LocationTrackerConfig()
            ).observe(
                this,
                Observer<Location?> { location ->
                    location?.let {
                        Log.d("Location", "Location - found - lat: ${it.latitude} lng: ${it.longitude}")
                        result.text = "Last Location - found\nlat: ${it.latitude}\nlng:${it.longitude}\naccuracy: ${it.accuracy}"
                    } ?: run {
                        Log.d("Location", "Location - no location found")
                        result.text = "Last Location - no location found"
                    }
                })
        }

        btn_tracker_stop.setOnClickListener {
            locationManager.stopLocationTracker()
        }

        btn_clear_result.setOnClickListener {
            result.text = "- Please choose action -"
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

    /**
     * handle activity result
     *
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        locationManager.onActivityResult(requestCode, resultCode, data)
            ?: super.onActivityResult(requestCode, resultCode, data)
    }

}
