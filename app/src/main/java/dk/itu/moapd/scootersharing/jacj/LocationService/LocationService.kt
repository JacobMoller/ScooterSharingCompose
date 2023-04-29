package dk.itu.moapd.scootersharing.jacj.services.Location

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import dk.itu.moapd.scootersharing.jacj.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.*
import kotlin.math.min

class LocationService: Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var locationClient: LocationClient

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        locationClient = DefaultLocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    lateinit var startDate: Date

    private fun start() {
        //Now
        startDate = Date()

        //Create foreground service notification
        val notification = NotificationCompat.Builder(this, "location")
            .setContentTitle("ScooterSharing tracking location...")
            .setContentText("Location: null")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        Log.d("HEEEE","Starting Location-trace");
        //Get location and update notification
        locationClient.getLocationUpdates(1000L)
            .catch { e -> e.printStackTrace() }
            .onEach { location ->
                val now = Date()

                val diff: Long = now.getTime() - startDate.getTime()
                val seconds = (diff / 1000)
                val minutes = seconds / 60
                val price = seconds * 0.025 + 5

                val lat = location.latitude
                val long = location.longitude
                locationTrace.add(LatLng(lat,long))
                //TODO: Add this to ride

                val updatedNotification = notification.setContentText(
                    "Price: $price DKK, Time: ${formatTime(minutes, seconds)}"
                )
                notificationManager.notify(1, updatedNotification.build())
            }
            .launchIn(serviceScope)

        //Start notification
        startForeground(1,notification.build())
    }

    private fun formatTime(minutes: Long, seconds: Long): String {
        var remainingSeconds = seconds % 60
        var date = "";
        if(minutes < 10)
            date += "0"
        date += minutes
        date += ":"
        if(remainingSeconds < 10)
            date += "0"
        date += remainingSeconds
        return date
    }

    private fun stop() {
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
        Log.d("HEEEE","Location-trace");
        for (location in locationTrace) {
            Log.d("HEEEE", location.toString())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        var locationTrace: MutableList<LatLng> = ArrayList()
    }

}