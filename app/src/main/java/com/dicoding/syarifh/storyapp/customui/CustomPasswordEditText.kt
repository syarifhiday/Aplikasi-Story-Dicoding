package com.dicoding.syarifh.storyapp.customui

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.google.android.material.textfield.TextInputLayout

class CustomPasswordEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatEditText(context, attrs, defStyleAttr) {

    private var textInputLayout: TextInputLayout? = null

    init {
        var parentView = parent
        while (parentView != null) {
            if (parentView is TextInputLayout) {
                textInputLayout = parentView
                break
            }
            parentView = parentView.parent
        }


    }
    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (s.length < 8) {
            setError("Password tidak boleh kurang dari 8 karakter", null)
        } else if (containsSpecialCharacters(s.toString())) {
            setError("Password tidak boleh mengandung karakter khusus", null)
        } else {
            error = null
        }
    }

    private fun containsSpecialCharacters(input: String): Boolean {
        val regex = Regex("[^a-zA-Z0-9 ]")
        return regex.containsMatchIn(input)
    }
}