package com.lambdasoup.notyet

import android.text.InputFilter
import android.text.Spanned

object DigitsFilter : InputFilter {
    override fun filter(source: CharSequence?, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int): CharSequence? {
        return source?.subSequence(start, end)?.filter { it.isDigit() }
    }
}
