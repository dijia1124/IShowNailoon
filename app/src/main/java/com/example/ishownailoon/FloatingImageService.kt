package com.example.ishownailoon

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import android.widget.ImageView
import java.util.Random

class FloatingImageService : Service() {
    private lateinit var windowManager: WindowManager
    private val mediaPlayers = mutableListOf<MediaPlayer>()  // Stores all MediaPlayer instances
    private val imageViews = mutableListOf<ImageView>()      // Stores all ImageView instances

    override fun onBind(intent: Intent?): IBinder? {
        // This service is not bound, so return null
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        val handler = Handler()
        val runnable = object : Runnable {
            override fun run() {
                // Create a new ImageView for each invocation
                val imageView = ImageView(this@FloatingImageService).apply {
                    setImageResource(R.drawable.nai_loong)  // Sets the image from resources
                }

                // Set parameters for the floating window
                val params = WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT
                ).apply {
                    gravity = Gravity.TOP or Gravity.LEFT
                    x = Random().nextInt(windowManager.defaultDisplay.width)  // Random x position
                    y = Random().nextInt(windowManager.defaultDisplay.height) // Random y position
                }

                // Add the ImageView to the WindowManager
                windowManager.addView(imageView, params)
                imageViews.add(imageView)  // Store the ImageView reference

                // Create and start a MediaPlayer for the image
                val mediaPlayer = MediaPlayer.create(this@FloatingImageService, R.raw.im_nai_loong).apply {
                    isLooping = true  // Set to loop the audio if needed
                    start()           // Start playback
                }
                mediaPlayers.add(mediaPlayer)  // Store the MediaPlayer reference

                // Schedule the next run of the ImageView and MediaPlayer creation
                handler.postDelayed(this, 666)  // Executes every 666 milliseconds
            }
        }
        handler.post(runnable)  // Start the Runnable task

        return START_STICKY  // Make service restart if it's terminated
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clean up all ImageViews when the service is destroyed
        imageViews.forEach { imageView ->
            windowManager.removeView(imageView)
        }
        // Stop and release all MediaPlayer resources
        mediaPlayers.forEach { mediaPlayer ->
            mediaPlayer.stop()
            mediaPlayer.release()
        }
    }
}
