package com.dinhnt.customdownloadapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dinhnt.customdownloadapp.databinding.ActivityDetailBinding
import com.dinhnt.customdownloadapp.utils.FileDownloadStatus

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        var fileName = intent.extras?.get(getString(R.string.file_download_description))
        var status =
            intent.extras?.get(getString(R.string.file_download_status))

        if (fileName != null && status != null) {
            binding.fileDescription = fileName as String
            binding.fileDownloadStatus = (status as FileDownloadStatus)
        }
        binding.okButton.setOnClickListener{
            finish()
        }
        setContentView(binding.root)
    }
}