package com.templatesitegroup.steelproductionai.base

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

object Extensions {

    fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (imm.isActive) {
            imm.hideSoftInputFromWindow(windowToken, 0)
        }
    }

    fun String.splitToFloatList(): List<Float> {
        return this.split(";").map { it.toFloat() }
    }
}
