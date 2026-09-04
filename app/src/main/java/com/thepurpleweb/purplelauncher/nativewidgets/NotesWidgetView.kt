package com.thepurpleweb.purplelauncher.nativewidgets

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.widget.EditText

/**
 * Fully local-first per spec section 15/16 — no network, no cloud sync,
 * just SharedPreferences. This is the simplest possible real (not fake)
 * native widget: no permission, no external dependency.
 */
class NotesWidgetView(context: Context) : NativeWidgetView(context) {

    private val prefs = context.applicationContext.getSharedPreferences(
        "purple_native_widgets", Context.MODE_PRIVATE
    )

    private val input = EditText(context).apply {
        hint = "Tap to write a note..."
        setTextColor(Color.WHITE)
        setHintTextColor(Color.rgb(120, 120, 120))
        textSize = 15f
        background = null
        gravity = Gravity.TOP
        setPadding(24, 20, 24, 20)
        setText(prefs.getString(KEY_NOTE_TEXT, "") ?: "")
    }

    init {
        addView(input, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))

        input.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
            override fun afterTextChanged(s: Editable?) {
                prefs.edit().putString(KEY_NOTE_TEXT, s?.toString().orEmpty()).apply()
            }
        })
    }

    override fun start() {}
    override fun stop() {}

    companion object {
        private const val KEY_NOTE_TEXT = "note_text"
    }
}
