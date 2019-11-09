package com.mobile.praaktishockey.ui.login.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.mobile.praaktishockey.R
import com.mobile.praaktishockey.domain.extension.onClick
import kotlinx.android.synthetic.main.dialog_calobrate_instruction.*

class InstructionDialog (context: Context): Dialog(context, R.style.BaseDialog) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCancelable(false)
        setContentView(R.layout.dialog_calobrate_instruction)
        tvOk.onClick {
            dismiss()
        }
    }

}