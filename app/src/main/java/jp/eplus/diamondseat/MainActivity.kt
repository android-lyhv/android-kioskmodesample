package jp.eplus.diamondseat

import android.annotation.TargetApi
import android.app.admin.DevicePolicyManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.os.Message
import android.view.KeyEvent
import android.webkit.HttpAuthHandler
import android.webkit.SslErrorHandler
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import jp.eplus.diamondseat.databinding.ActivityMainBinding

open class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val systemHandlerViewModel: SystemHandlerViewModel by viewModels()
    private val spUtils by lazy {
        SPUtils(this)
    }

    companion object {
        const val SEAT_ID_EXTRA = "SEAT_ID"
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, p1: Intent?) {
            Toast.makeText(context, p1?.action ?: "", Toast.LENGTH_SHORT).show()
            val data = intent.getStringExtra("KioskAdminReceiver") ?: ""
            // if (data.isNotBlank()) {
            enableKioskMode(true)
            // }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        registerReceiver(receiver, IntentFilter("KioskAdminReceiver"))
        with(binding.webview) {
            // Init web setting
            with(settings) {
                setSupportZoom(true)
                javaScriptEnabled = true
                setGeolocationEnabled(true)
                loadWithOverviewMode = true
                useWideViewPort = true
                domStorageEnabled = true
                allowFileAccess = true
                allowContentAccess = true
                @Suppress("DEPRECATION")
                saveFormData = false
                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

            }

            webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                    // Page loading started
                    // Do something
                }

                override fun onPageFinished(view: WebView, url: String) {
                    // Page loading finished
                    // Enable disable back forward button
                }

                override fun onReceivedSslError(
                    view: WebView?,
                    handler: SslErrorHandler?,
                    error: SslError?
                ) {
                    handler?.proceed()
                }

                override fun onFormResubmission(
                    view: WebView,
                    dontResend: Message,
                    resend: Message
                ) {
                    resend.sendToTarget()
                }

                @SuppressWarnings("deprecation")
                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                    val uri = Uri.parse(url)
                    return overrideUrlLoading(uri)
                }

                override fun onReceivedHttpAuthRequest(
                    view: WebView?,
                    handler: HttpAuthHandler?,
                    host: String?,
                    realm: String?
                ) {
                    handler?.proceed(BuildConfig.WEB_URL_USER, BuildConfig.WEB_URL_PASSWORD)
                    super.onReceivedHttpAuthRequest(view, handler, host, realm)
                }

                @TargetApi(Build.VERSION_CODES.N)
                override fun shouldOverrideUrlLoading(
                    view: WebView,
                    request: WebResourceRequest
                ): Boolean {
                    val uri = request.url
                    return overrideUrlLoading(uri)
                }
            }
        }
        binding.tvSeatId.text = spUtils.seatId
        binding.tvHideLock.setOnClickListener {
            systemHandlerViewModel.canUnLockScreen {
                DialogInputPassword.newInstance {
                    enableKioskMode(false)
                    finish()
                }.show(supportFragmentManager, null)
            }
        }
        sendBroadcast(Intent().apply { action = "KioskAdminReceiver" })
        binding.webview.loadUrl(BuildConfig.WEB_URL)
    }

    protected open fun overrideUrlLoading(
        uri: Uri
    ): Boolean {
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    //kiosk mode
    fun enableKioskMode(enabled: Boolean) {
        Toast.makeText(this, "Kiosk Mode $enabled", Toast.LENGTH_SHORT).show()
        val deviceAdmin = ComponentName(this, KioskAdminReceiver::class.java)
        val mDpm =
            getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        try {
            if (enabled) {
                mDpm.setLockTaskPackages(deviceAdmin, arrayOf(packageName))
                if (mDpm.isLockTaskPermitted(this.packageName)) {
                    startLockTask()
                }
            } else {
                if (mDpm.isDeviceOwnerApp(packageName)) {
                    try {
                        //  mDpm.clearDeviceOwnerApp(packageName)
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
                    if (binding.webview.canGoBack()) {
                        binding.webview.goBack()
                    }
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}