package jp.eplus.diamondseat

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class KioskAdminReceiver : DeviceAdminReceiver() {
    override fun onEnabled(context: Context, intent: Intent) {
        Toast.makeText(
            context,
            context.getString(R.string.device_admin_enabled),
            Toast.LENGTH_SHORT
        ).show()

        context.sendBroadcast(Intent().apply {
            action = "KioskAdminReceiver"
            putExtra("KioskAdminReceiver", "KioskAdminReceiver")
        })

    }

    override fun onDisabled(context: Context, intent: Intent) {
        Toast.makeText(
            context,
            context.getString(R.string.device_admin_disabled),
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onDisableRequested(context: Context, intent: Intent): CharSequence? {
        return context.getString(R.string.device_admin_warning)
    }
}