package com.mobile.gympraaktis.domain.common

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable

import com.mobile.gympraaktis.R

class ProgressLoadingDialog(context: Context) : Dialog(context) {
    init {
        setContentView(R.layout.loading_view)
        if (window != null)
            window!!.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        setCanceledOnTouchOutside(false)
        setCancelable(false)
    }

    override fun onBackPressed() {
        //        super.onBackPressed();
    }
}