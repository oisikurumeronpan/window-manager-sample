package com.arstkn.wms

import android.annotation.TargetApi
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // API 23 以上であればPermission chekを行う
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        checkPermission()
        //}

        // Serviceを開始するためのボタン
        val buttonStart = findViewById<Button>(R.id.button_start)
        buttonStart.setOnClickListener {
            val intent = Intent(application, TestService::class.java)
            // Serviceの開始
            // API26以上
            startForegroundService(intent)
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun checkPermission() {
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName"))
            startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE)
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                Log.d("debug", "SYSTEM_ALERT_WINDOW permission not granted...")
                // SYSTEM_ALERT_WINDOW permission not granted...
                // nothing to do !
            }
        }
    }

    companion object {
        var OVERLAY_PERMISSION_REQ_CODE = 1000
    }
}