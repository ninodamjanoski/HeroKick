package com.endumedia.herokick.util

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager


/**
 * Created by Nino on 28.08.19
 */
object UiUtils {


    fun hideKeyboard(context: Context, view: View) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }


    fun convertDpToPixels(context: Context, i: Int): Int {
        return TypedValue.applyDimension(1, i.toFloat(), context.resources.displayMetrics).toInt()
    }
}