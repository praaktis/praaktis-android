package com.mobile.gympraaktis.domain.extension

import android.content.Context
import com.mobile.gympraaktis.R
import com.mobile.gympraaktis.domain.common.pref.SettingsStorage
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import java.text.SimpleDateFormat
import java.util.*

fun Context.isTablet() = resources.getBoolean(R.bool.isTablet)
fun androidx.fragment.app.Fragment.isTablet() = resources.getBoolean(R.bool.isTablet)

fun Calendar.dateOfBirthFormat(): String {
    val format = SimpleDateFormat("dd/MM/yyyy")
    return format.format(time)
}

fun Calendar.dateYYYY_MM_DD(): String {
    val format = SimpleDateFormat("yyyy-MM-dd")
    return format.format(time)
}

fun LocalDate.formatMMMddYYYY(): String {
    return this.format(DateTimeFormatter.ofPattern("MMM d, yyyy", Locale(SettingsStorage.instance.getLanguage())))
}

fun String.MMMddYYYYtoLocalDate():LocalDate {
    return LocalDate.parse(this, DateTimeFormatter.ofPattern("MMM d, yyyy", Locale(SettingsStorage.instance.getLanguage())))
}