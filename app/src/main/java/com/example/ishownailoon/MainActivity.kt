package com.example.ishownailoon

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Request overlay permission for floating windows
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName"))
            startActivityForResult(intent, 1)  // Start activity for result to handle user response
        } else {
            startFloatingImageService()  // Start the floating image service if permission already granted
        }
    }

    private fun startFloatingImageService() {
        startService(Intent(this, FloatingImageService::class.java))  // Start the floating image service
        finish() // Close activity after starting the service
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {  // Check if the returned result is from our overlay permission request
            if (Settings.canDrawOverlays(this)) {
                startFloatingImageService()  // Start the service if permission was granted
            } else {
                // Permission not granted, close the app
                finish()  // End the application
            }
        }
    }
}
