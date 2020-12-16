package jp.eplus.diamondseat

import android.content.Context
import android.content.SharedPreferences

@Suppress("unused")
class SPUtils(context: Context) {

    private val sp: SharedPreferences =
        context.getSharedPreferences("oder-android", Context.MODE_PRIVATE)

    var deviceId: String?
        get() = sp.getString(KEY_DEVICE_ID, null)
        set(deviceId) = sp.edit().putString(KEY_DEVICE_ID, deviceId).apply()
    var seatId: String?
        get() = sp.getString(KEY_SEAT_ID, null)
        set(deviceId) = sp.edit().putString(KEY_SEAT_ID, deviceId).apply()

    companion object {
        private const val KEY_DEVICE_ID = "pref_key_device_id"
        private const val KEY_SEAT_ID = "pref_key_seat_id"
    }
}
