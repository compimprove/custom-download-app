package com.dinhnt.customdownloadapp.utils

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import com.dinhnt.customdownloadapp.DetailActivity
import com.dinhnt.customdownloadapp.R


private const val NOTIFICATION_ID = 0
private const val REQUEST_CODE = 0
private const val FLAGS = 0

enum class FileDownloadStatus { Success, Fail }

fun NotificationManager.sendFileDownloadNotification(
    channelId: String,
    fileDownloadDescription: String,
    status: FileDownloadStatus,
    context: Context
) {
    val contentIntent = Intent(context, DetailActivity::class.java)
    contentIntent.putExtra(
        context.getString(R.string.file_download_description),
        fileDownloadDescription
    )
    contentIntent.putExtra(
        context.getString(R.string.file_download_status),
        status
    )
    val imageDownload = BitmapFactory.decodeResource(
        context.resources,
        R.drawable.download_cloud
    )
    val bigPicStyle = NotificationCompat.BigPictureStyle()
        .bigPicture(imageDownload)
        .bigLargeIcon(null)

    val contentPendingIntent = PendingIntent.getActivity(
        context,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val builder = NotificationCompat.Builder(
        context,
        channelId
    )
        .setSmallIcon(R.drawable.ic_download_cloud)
        .setContentTitle("${context.getString(R.string.download)} ${status.name}")
        .setContentText(fileDownloadDescription)
        .setContentIntent(contentPendingIntent)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setStyle(bigPicStyle)
        .setAutoCancel(true)
        .addAction(
            R.drawable.ic_download_cloud,
            context.getString(R.string.download_status),
            contentPendingIntent
        )
    notify(NOTIFICATION_ID, builder.build())
}

fun NotificationManager.cancelNotifications() {
    cancelAll()
}
