package com.dicoding.storyapp.customview
import com.dicoding.storyapp.R
import com.google.android.material.textfield.TextInputEditText
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet

class InputPassword : TextInputEditText {
    constructor(
        context: Context,
        attrs: AttributeSet,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    constructor(context: Context) : super(context) { init(context) }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) { init(context) }


    private fun init(context: Context) {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val password = s.toString()
                error = if (password.length < 8) {
                    context.getString(R.string.password_validity)
                } else { null } }

            override fun afterTextChanged(s: Editable?) {}
        })
    }
}