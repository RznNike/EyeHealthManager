package ru.rznnike.eyehealthmanager.app.utils.extensions

import android.content.Intent
import android.os.Build
import android.os.Parcelable
import androidx.fragment.app.Fragment

inline fun <reified T: Parcelable> Fragment.getParcelableArg(key: String): T? = if (Build.VERSION.SDK_INT >= 33) {
    arguments?.getParcelable(key, T::class.java)
} else {
    @Suppress("DEPRECATION")
    arguments?.getParcelable(key)
}

inline fun <reified T: Parcelable> Fragment.getParcelableListArg(key: String): List<T> = if (Build.VERSION.SDK_INT >= 33) {
    arguments?.getParcelableArrayList(key, T::class.java)
} else {
    @Suppress("DEPRECATION")
    arguments?.getParcelableArrayList(key)
} ?: emptyList()

fun Fragment.getBooleanArg(key: String): Boolean = arguments?.getBoolean(key) == true

fun Fragment.getStringArg(key: String): String? = arguments?.getString(key)

fun Fragment.getStringArg(key: String, defaultValue: String): String = arguments?.getString(key, defaultValue) ?: defaultValue

fun Fragment.getLongArg(key: String): Long = arguments?.getLong(key) ?: 0

fun Fragment.getLongArg(key: String, defaultValue: Long): Long = arguments?.getLong(key, defaultValue) ?: defaultValue

fun Fragment.getIntArg(key: String): Int = arguments?.getInt(key) ?: 0

fun Fragment.getIntArg(key: String, defaultValue: Int): Int = arguments?.getInt(key, defaultValue) ?: defaultValue

inline fun <reified T: Parcelable> Intent.getParcelableExtraCompat(key: String): T? = if (Build.VERSION.SDK_INT >= 33) {
    getParcelableExtra(key, T::class.java)
} else {
    @Suppress("DEPRECATION")
    getParcelableExtra(key)
}
