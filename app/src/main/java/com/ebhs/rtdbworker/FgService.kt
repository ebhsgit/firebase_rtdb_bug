package com.ebhs.rtdbworker

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class FgService : Service() {

    companion object {
        const val TAG = "RtdbWorker.FgService"

        const val channelId = "fgService-channel"
        const val channelName = "Foreground Service"
        const val notificationTitle = "Foreground Service"
        const val notificationText = "Sticky. Does nothing"

        fun start(context: Context) {
            Log.d(TAG, "Start service")
            val intent = Intent(context, FgService::class.java)
            context.startForegroundService(intent)
        }

        fun stop(context: Context) {
            Log.d(TAG, "Stop service")
            val intent = Intent(context, FgService::class.java)
            context.stopService(intent)
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")

        createNotificationChannel()
        val notification = createNotification()
        startForeground(1, notification)

        return START_STICKY
    }

    override fun onCreate() {
        Log.d(TAG, "onCreate")
        super.onCreate()
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        super.onDestroy()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        Log.d(TAG, "onTaskRemoved")
        super.onTaskRemoved(rootIntent)
    }

    private fun createNotificationChannel() {
        val notMgr = NotificationManagerCompat.from(this)
        val fsChannel = notMgr.getNotificationChannel(channelId)
        if (fsChannel != null) {
            return
        }

        val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
        channel.enableVibration(false)
        channel.setShowBadge(false)

        notMgr.createNotificationChannel(channel)
    }

    private fun createNotification(): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle(notificationTitle)
            .setContentText(notificationText)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.BigTextStyle().bigText(notificationText))
            .build()
    }
}