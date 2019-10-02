package com.endumedia.notes.util

import android.content.Context
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
}