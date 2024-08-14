package com.facecapture.prod

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        checkCameraPermission()
    }

    private val mPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                gotoCaptureActivity()
            } else {
                finish()
            }
        }

    private fun checkCameraPermission() {
        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            gotoCaptureActivity()
            return
        }
        mPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    private fun gotoCaptureActivity() {
        startActivity(Intent(this, CaptureActivity::class.java))
        finish()
    }
}