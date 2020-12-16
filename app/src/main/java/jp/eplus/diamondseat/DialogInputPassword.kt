package jp.eplus.diamondseat

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import jp.eplus.diamondseat.databinding.DialogInputPasswordBinding

class DialogInputPassword(private val callback: () -> Unit) : DialogFragment() {
    companion object {
        fun newInstance(callback: () -> Unit): DialogInputPassword {
            return DialogInputPassword {
                callback()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.setCanceledOnTouchOutside(false)
        dialog?.setCancelable(false)
    }

    private lateinit var binding: DialogInputPasswordBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogInputPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        binding.tvError.isGone = true
        binding.btnDone.click {
            onCheckPasswordAnDismiss()
        }
        binding.edtPassword.addTextChangedListener {
            binding.tvError.isGone = true
        }
        binding.imgClose.click {
            dismiss()
        }
        binding.edtPassword.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                if (event?.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    onCheckPasswordAnDismiss()
                    return true
                }
                return false
            }
        })
    }

    private fun onCheckPasswordAnDismiss() {
        val password = binding.edtPassword.text.toString()
        if (password == BuildConfig.UNLOCK_PASSWORD) {
            callback.invoke()
            dismiss()
        } else {
            binding.tvError.isVisible = true
        }
    }
}