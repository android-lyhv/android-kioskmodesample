package com.ervn.kisokmodesample

import android.app.admin.DevicePolicyManager
import android.content.*
import android.os.Bundle
import android.view.KeyEvent
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, p1: Intent?) {
            Toast.makeText(context, p1?.action ?: "", Toast.LENGTH_SHORT).show()
            val data = intent.getStringExtra("KioskAdminReceiver") ?: ""
            // if (data.isNotBlank()) {
            enableKioskMode(true)
            // }
        }

    }
    private lateinit var webView: WebView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerReceiver(receiver, IntentFilter("KioskAdminReceiver"))
        webView = WebView(this)
        webView.settings.apply {
            javaScriptEnabled = true
        }
        webView.loadUrl("https://google.com")
        webView.webViewClient = WebViewClient()
        setContentView(webView)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    //kiosk mode
    fun enableKioskMode(enabled: Boolean) {
        Toast.makeText(this, "Kiosk Mode enable", Toast.LENGTH_SHORT).show()
        val deviceAdmin = ComponentName(this, KioskAdminReceiver::class.java)
        val mDpm =
            getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        try {
            if (enabled) {
                if (mDpm.isDeviceOwnerApp(packageName)) {
                    mDpm.setLockTaskPackages(deviceAdmin, arrayOf(packageName))
                    if (mDpm.isLockTaskPermitted(this.packageName)) {
                        startLockTask()
                    }
                }
            } else {
                if (mDpm.isDeviceOwnerApp(packageName)) {
                    try {
                        mDpm.clearDeviceOwnerApp("com.ervn.kisokmodesample")
                        stopLockTask()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        stopLockTask()
                    }
                } else {
                    stopLockTask()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            when (keyCode) {
                KeyEvent.KEYCODE_BACK -> {
                    if (webView.canGoBack()) {
                        webView.goBack()
                    } else {
                        finish()
                    }
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}