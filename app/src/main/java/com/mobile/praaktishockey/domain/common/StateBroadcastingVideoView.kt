package com.mobile.praaktishockey.domain.common

import android.content.Context
import android.util.AttributeSet
import android.widget.VideoView

class StateBroadcastingVideoView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : VideoView(context, attrs, defStyleAttr) {

    interface PlayPauseListener {
        fun onPlay()
        fun onPause()
    }

    private var mListener: PlayPauseListener? = null

    override fun pause() {
        super.pause()
        mListener?.onPause()
    }

    override fun start() {
        super.start()
        mListener?.onPlay()
    }

    fun setPlayPauseListener(listener: PlayPauseListener) {
        mListener = listener
    }

}