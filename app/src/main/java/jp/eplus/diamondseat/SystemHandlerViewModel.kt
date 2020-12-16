package jp.eplus.diamondseat

import androidx.lifecycle.ViewModel

class SystemHandlerViewModel : ViewModel() {
    private var limitTimeClick: Long = 0

    companion object {
        const val MAX_TAP = 10
    }

    private var countTap = 1

    fun canUnLockScreen(available: () -> Unit) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - limitTimeClick <= 500) {
            countTap += 1
        } else {
            countTap = 1
        }
        limitTimeClick = currentTime
        if (countTap >= MAX_TAP) {
            countTap = 1
            available()
        }
    }
}

