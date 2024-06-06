package com.dicoding.syarifh.storyapp.customui

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.google.android.material.textfield.TextInputLayout

class CustomNameEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatEditText(context, attrs, defStyleAttr) {

    private var textInputLayout: TextInputLayout? = null
    private var valid: Boolean = false

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

    override fun onTextChanged(
        text: CharSequence?,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        if (onlyAlphabet(text.toString())) {
            setError("Nama hanya boleh mengandung huruf A-Z", null)
        } else {
            error = null

        }
    }

    private fun onlyAlphabet(input: String): Boolean {
        val regex = Regex("[^a-zA-Z ]")
        return regex.containsMatchIn(input)
    }


}
