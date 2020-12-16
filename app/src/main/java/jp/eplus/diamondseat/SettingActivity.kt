package jp.eplus.diamondseat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import jp.eplus.diamondseat.databinding.SettingUiBinding

class SettingActivity : AppCompatActivity() {
    private lateinit var binding: SettingUiBinding
    private val systemHandlerViewModel: SystemHandlerViewModel by viewModels()
    private val spUtils by lazy {
        SPUtils(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SettingUiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }
    private fun initView() {
        with(binding) {
            tvDeviceId.text = DeviceUtils.getDeviceUUID(this@SettingActivity, spUtils)
            tvUrl.text = BuildConfig.WEB_URL
        }
        binding.btnSave.click {
            spUtils.seatId = binding.tvSeatId.text.toString().trim()
            startActivity(Intent(this, MainActivity::class.java))
        }
        if (spUtils.seatId?.isNotBlank() == true) {
            startActivity(Intent(this, MainActivity::class.java))
        }
        binding.tvHideLock.setOnClickListener {
            systemHandlerViewModel.canUnLockScreen {
                DialogInputPassword.newInstance {
                    finish()
                }.show(supportFragmentManager, null)
            }
        }
    }
    fun onCheckDeepLink(){
        val uri = intent.data
        Log.d("DeepLink", "${uri?.lastPathSegment}")
    }
}