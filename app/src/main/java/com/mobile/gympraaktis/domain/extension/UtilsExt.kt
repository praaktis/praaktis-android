package com.mobile.gympraaktis.domain.extension

import android.content.res.Resources

val Int.px: Int get() = (this / Resources.getSystem().displayMetrics.density).toInt()
val Int.dp: Int get() = (this * Resources.getSystem().displayMetrics.density).toInt()

val Float.dp: Int get() = (this * Resources.getSystem().displayMetrics.density).toInt()
val Float.px: Int get() = (this / Resources.getSystem().displayMetrics.density).toInt()

fun String.removeDuplicateWhiteSpaces() = this.replace("\\s+".toRegex(), " ")