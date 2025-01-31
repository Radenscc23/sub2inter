package com.dicoding.storyapp.customview
import com.dicoding.storyapp.R
import com.google.android.material.textfield.TextInputEditText
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns


class InputEmail : TextInputEditText {

    constructor(
        context: Context,
        attrs: AttributeSet,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    constructor(context: Context) : super(context) { init(context) }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) { init(context) }



    private fun init (context: Context) {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val email = s.toString()
                val isValidEmail =
                    email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()

                error = if (isValidEmail) {
                    null
                } else { context.getString(R.string.email_validity) } }

            override fun afterTextChanged(s: Editable?) {}
        })
    }
}