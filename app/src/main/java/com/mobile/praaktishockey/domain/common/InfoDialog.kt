package com.mobile.praaktishockey.domain.common

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.mobile.praaktishockey.R
import com.mobile.praaktishockey.domain.extension.onClick
import kotlinx.android.synthetic.main.dialog_info.*

class InfoDialog (context: Context,
                  private val message: String,
                  private val listener: InfoDialogListener): Dialog(context, R.style.AppAlertDialogTheme) {

    interface InfoDialogListener {
        fun onOkClicked()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_info)
        setCanceledOnTouchOutside(false)
        tvCancel.onClick {
            dismiss()
        }
        tvOk.onClick {
            listener.onOkClicked()
            dismiss()
        }
        tvMessage.text = message
    }

}