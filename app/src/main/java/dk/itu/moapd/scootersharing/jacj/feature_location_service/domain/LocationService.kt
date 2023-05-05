package dk.itu.moapd.scootersharing.jacj.feature_location_service.domain

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.LocationServices
import dk.itu.moapd.scootersharing.jacj.MainActivity
import dk.itu.moapd.scootersharing.jacj.R
import dk.itu.moapd.scootersharing.jacj.core.domain.model.Coords
import dk.itu.moapd.scootersharing.jacj.feature_location_service.data.data_source.DefaultLocationClient
import dk.itu.moapd.scootersharing.jacj.feature_location_service.domain.repository.LocationClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.Date


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

    private lateinit var startDate: Date

    private fun start() {
        //Now
        startDate = Date()

        //Create foreground service notification
        val notification = NotificationCompat.Builder(this, "location")
            .setContentTitle("ScooterSharing tracking location...")
            .setContentText("Location: null")
            .setSmallIcon(R.drawable.logo_big)
            .setOngoing(true)

        val contentIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT
        )

        notification.setContentIntent(contentIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        //Get location and update notification
        locationClient.getLocationUpdates(1000L)
            .catch { e -> e.printStackTrace() }
            .onEach { location ->
                val now = Date()

                val diff: Long = now.time - startDate.time
                val seconds = (diff / 1000)
                val minutes = seconds / 60
                price = minutes * 2 + 10

                val lat = location.latitude
                val long = location.longitude
                locationTrace.add(Coords(lat,long))

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
        val remainingSeconds = seconds % 60
        var date = ""
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
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        var locationTrace: MutableList<Coords> = ArrayList()
        var price: Long = 0
    }

}