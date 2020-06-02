package com.arstkn.wms

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.util.Log
import android.view.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.test_layer.view.*


class TestService : Service() {
    private var view: View? = null
    private var windowManager: WindowManager? = null
    private var dpScale = 0
    override fun onCreate() {
        super.onCreate()

        // dipを取得
        dpScale = resources.displayMetrics.density.toInt()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        // startForegroundService() -----
        val context = applicationContext
        val channelId = "default"
        val title = context.getString(R.string.app_name)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Notification　Channel 設定
        val channel = NotificationChannel(
            channelId, title, NotificationManager.IMPORTANCE_DEFAULT
        )
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(channel)
            val notification =
                Notification.Builder(context, channelId)
                    .setContentTitle(title) // android標準アイコンから
                    .setSmallIcon(android.R.drawable.btn_star)
                    .setContentText("APPLICATION_OVERLAY")
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setWhen(System.currentTimeMillis())
                    .build()

            // startForeground
            startForeground(1, notification)
        }
        // ----- startForegroundService()


        // inflaterの生成
        val layoutInflater = LayoutInflater.from(this)
        val typeLayer = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        windowManager = applicationContext
            .getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            typeLayer, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        )

        // 右上に配置
        params.gravity = Gravity.TOP or Gravity.START
        params.x = 0 * dpScale // 20dp
        params.y = 0 * dpScale // 80dp
        params.width = resources.displayMetrics.widthPixels

        // レイアウトファイルからInfalteするViewを作成
        val nullParent: ViewGroup? = null
        view = layoutInflater.inflate(R.layout.test_layer, nullParent)

        view!!.answer.setOnClickListener {
            Log.d("debug", "answer")
        }
        view!!.decline.setOnClickListener {
            Log.d("debug","decline")
        }

        // ViewにTouchListenerを設定する
        view!!.setOnTouchListener { v, event ->
            Log.d("debug", "onTouch")
            if (event.action == MotionEvent.ACTION_DOWN) {
                Log.d("debug", "ACTION_DOWN")
            }
            if (event.action == MotionEvent.ACTION_UP) {
                Log.d("debug", "ACTION_UP")

                // warning: override performClick()
                view!!.performClick()

                // Serviceを自ら停止させる
                stopSelf()
            }
            false
        }

        // Viewを画面上に追加
        windowManager!!.addView(view, params)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("debug", "onDestroy")
        // Viewを削除
        windowManager!!.removeView(view)
    }

    override fun onBind(intent: Intent): IBinder? {
        // TODO Auto-generated method stub
        return null
    }
}