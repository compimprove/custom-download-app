package com.dinhnt.customdownloadapp

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.dinhnt.customdownloadapp.databinding.ActivityMainBinding
import com.dinhnt.customdownloadapp.utils.FileDownloadStatus
import com.dinhnt.customdownloadapp.utils.sendFileDownloadNotification


class MainActivity : AppCompatActivity() {
    private lateinit var downloadManager: DownloadManager
    private lateinit var binding: ActivityMainBinding
    private var downloadStatus: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        createChannel(
            getString(R.string.download_channel),
            "Download Notification",
            "Download Completed"
        )
        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        binding.downloadButton.setOnClickListener {
            downloadFile()
        }
    }

    private fun downloadFile() {
        binding.downloadButton.createLoadingAnimation()
        when (binding.chooseRepository.checkedRadioButtonId) {
            R.id.glide_radio -> startDownload(
                "https://github.com/bumptech/glide",
                getString(R.string.glide)
            )
            R.id.load_app_radio -> startDownload(
                "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter",
                getString(R.string.load_app)
            )
            R.id.retrofit_radio -> startDownload(
                "https://github.com/square/retrofit",
                getString(R.string.retrofit)
            )
            else -> notifyChooseDownloadOption()
        }
    }

    private fun notifyChooseDownloadOption() {
        Toast.makeText(this, getString(R.string.selecte_file_to_download), Toast.LENGTH_SHORT)
            .show()
        binding.downloadButton.finishLoadingAnimation()
    }


    private fun startDownload(url: String, fileDescription: String) {
        val connectivityManager =
            applicationContext.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (connectivityManager.activeNetwork == null) {
                onReceiveDownloadStatus(fileDescription, FileDownloadStatus.Fail)
                return
            }
        } else {
            val nInfo = connectivityManager.getActiveNetworkInfo()
            val connected = nInfo != null && nInfo.isAvailable && nInfo.isConnected
            if (!connected) {
                onReceiveDownloadStatus(fileDescription, FileDownloadStatus.Fail)
                return
            }
        }
        val uri = Uri.parse(url)
        val request = DownloadManager.Request(uri)
            .setTitle(fileDescription)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDescription(getString(R.string.download_description))
        val downloadID =
            downloadManager.enqueue(request)
        registerReceiver(
            DownloadReceiver(
                downloadID
            ) { status -> onReceiveDownloadStatus(fileDescription, status) },
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        )
    }

    private fun onReceiveDownloadStatus(fileDescription: String, status: FileDownloadStatus) {
        val notificationManager = ContextCompat.getSystemService(
            this,
            NotificationManager::class.java
        ) as NotificationManager
        binding.downloadButton.finishLoadingAnimation()
        notificationManager.sendFileDownloadNotification(
            getString(R.string.download_channel),
            fileDescription,
            status,
            this
        )
    }


    private fun createChannel(channelId: String, channelName: String, channelDescription: String) {
        // TODO: Step 1.6 START create a channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
                .apply {
                    setShowBadge(false)
                }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = channelDescription
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )

            notificationManager.createNotificationChannel(notificationChannel)

        }
        // TODO: Step 1.6 END create channel
    }
}


class DownloadReceiver(
    private val downloadID: Long,
    private val callBack: (status: FileDownloadStatus) -> Unit
) :
    BroadcastReceiver() {
    @SuppressLint("Range")
    override fun onReceive(context: Context?, intent: Intent?) {
        try {
            val downloadManager = context?.getSystemService(Context.DOWNLOAD_SERVICE)
                    as DownloadManager
            val cursor = downloadManager.query(
                DownloadManager
                    .Query()
                    .setFilterById(downloadID)
            )
            if (cursor.moveToFirst()) {
                val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                if (status == DownloadManager.STATUS_SUCCESSFUL) {
                    callBack(FileDownloadStatus.Success)
                } else {
                    callBack(FileDownloadStatus.Fail)
                }
            } else {
                callBack(FileDownloadStatus.Fail)
            }
        } catch (e: Exception) {
            Log.e("onReceive", e.toString())
            callBack(FileDownloadStatus.Fail)
        }

    }
}


