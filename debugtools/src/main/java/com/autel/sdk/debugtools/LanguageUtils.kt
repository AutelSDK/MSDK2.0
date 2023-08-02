package com.autel.sdk.debugtools

import android.app.Activity
import android.content.Context
import android.os.Build
import java.util.*


object LanguageUtils {

    var defaultLanguage = Locale.US
    var chinesLanguage = Locale.CHINESE
    var ukranianLanguage = Locale("uk","UA")

    fun setLocale(activity: Activity, languageCode: String?) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val resources = activity.resources
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }



    private fun getCurrentLocale(context: Context): Locale {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.configuration.locales.get(0)
        } else {
            // For older Android versions, use the deprecated method
            context.resources.configuration.locale
        }
    }

    fun isDefaultLanguage(context: Context): Boolean {
        val currentLocale = getCurrentLocale(context)
        return currentLocale == Locale.US
    }

    fun isChineseLanguage(context: Context): Boolean {
        val currentLocale = getCurrentLocale(context)
        return currentLocale == Locale.CHINESE
    }

}