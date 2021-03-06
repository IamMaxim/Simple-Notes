package com.simplemobiletools.notes.adapters

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.simplemobiletools.commons.extensions.setText
import com.simplemobiletools.commons.extensions.setTextSize
import com.simplemobiletools.notes.R
import com.simplemobiletools.notes.R.id.widget_text_holder
import com.simplemobiletools.notes.extensions.config
import com.simplemobiletools.notes.extensions.dbHelper
import com.simplemobiletools.notes.extensions.getNoteStoredValue
import com.simplemobiletools.notes.extensions.getTextSize
import com.simplemobiletools.notes.helpers.GRAVITY_CENTER
import com.simplemobiletools.notes.helpers.GRAVITY_RIGHT
import com.simplemobiletools.notes.helpers.OPEN_NOTE_ID

class WidgetAdapter(val context: Context) : RemoteViewsService.RemoteViewsFactory {
    private val textIds = arrayOf(R.id.widget_text_left, R.id.widget_text_center, R.id.widget_text_right)
    val config = context.config
    private val widgetTextColor = config.widgetTextColor

    override fun getViewAt(position: Int): RemoteViews {
        val views = RemoteViews(context.packageName, R.layout.widget_text_layout).apply {
            val note = context.dbHelper.getNote(context.config.widgetNoteId)
            if (note != null) {
                val noteText = context.getNoteStoredValue(note) ?: ""
                val textSize = context.getTextSize() / context.resources.displayMetrics.density
                for (id in textIds) {
                    setText(id, noteText)
                    setTextColor(id, widgetTextColor)
                    setTextSize(id, textSize)
                    setViewVisibility(id, View.GONE)
                }
            }

            Intent().apply {
                putExtra(OPEN_NOTE_ID, context.config.widgetNoteId)
                setOnClickFillInIntent(widget_text_holder, this)
            }

            setViewVisibility(getProperTextView(context), View.VISIBLE)
        }
        return views
    }

    private fun getProperTextView(context: Context) = when (context.config.gravity) {
        GRAVITY_CENTER -> R.id.widget_text_center
        GRAVITY_RIGHT -> R.id.widget_text_right
        else -> R.id.widget_text_left
    }

    override fun onCreate() {}

    override fun getLoadingView() = null

    override fun getItemId(position: Int) = position.toLong()

    override fun onDataSetChanged() {}

    override fun hasStableIds() = true

    override fun getCount() = 1

    override fun getViewTypeCount() = 1

    override fun onDestroy() {}
}
