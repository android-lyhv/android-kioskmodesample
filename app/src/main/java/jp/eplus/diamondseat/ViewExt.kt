package jp.eplus.diamondseat

import android.os.SystemClock
import android.view.View

fun View.click(debounceTime: Long = 1000L, action: (view: View) -> Unit) {
    this.setOnClickListener(object : View.OnClickListener {
        private var lastClickTime: Long = 0
        override fun onClick(v: View) {
            if (SystemClock.elapsedRealtime() - lastClickTime < debounceTime) {
                lastClickTime = SystemClock.elapsedRealtime()
                return
            }
            action(v)
            lastClickTime = SystemClock.elapsedRealtime()
        }
    })
}