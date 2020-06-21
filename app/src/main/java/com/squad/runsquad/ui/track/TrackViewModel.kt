package com.squad.runsquad.ui.track

import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.squad.runsquad.data.model.TrackState
import com.squad.runsquad.util.aggregateValue
import java.util.concurrent.TimeUnit

class TrackViewModel : ViewModel() {

    /**
     * Distance travelled in km - e.g. 1.3 = 1km 300 meters
     */
    val distanceTraveled = MutableLiveData<Float>()

    /**
     * Time elapsed in milliseconds
     */
    val timeElapsed = MutableLiveData<Long>()

    /**
     * average pace in "time" - e.g. 2.4 = 2 minutes 40 seconds
     */
    val averagePace = MutableLiveData<Float>()

    val isRunning = MutableLiveData<TrackState>()

    private var lastLocation: Location? = null

    /**
     * First entry point to update UI. Here we are receiving location from activity and delegating
     * business logic to other functions as appropriate and updating the views.
     */
    fun updateLocation(location: Location) {

        //TODO - simplify this updating distance logic
        if (lastLocation == null) {
            lastLocation = location
            if (distanceTraveled.value == null) distanceTraveled.postValue(0F)
            return
        }

        updateDistanceTravelled(lastLocation!!.distanceTo(location))
        lastLocation = location
    }

    private fun calculateAveragePace(timeMillis: Long, distance: Float) {

        val minutes = TimeUnit.MILLISECONDS.toMinutes(timeMillis)
        val seconds = (timeMillis / 1000) % 60

        val timeDecimal = "$minutes.$seconds".toFloat()

        averagePace.postValue(timeDecimal / distance)
    }

    private fun updateDistanceTravelled(value: Float) {
        distanceTraveled.aggregateValue((value / 1000))
    }

    fun updateTime(time: Long) {
        timeElapsed.postValue(time)
        distanceTraveled.value?.let {
            calculateAveragePace(time, it)
        }
    }

    fun start() {
        isRunning.postValue(TrackState.ACTIVE)
    }

    fun pause() {
        isRunning.postValue(TrackState.INACTIVE)
        lastLocation = null
    }

    fun resume() {
        isRunning.postValue(TrackState.ACTIVE)
    }

    fun stop() {
        isRunning.postValue(TrackState.INACTIVE)
    }
}