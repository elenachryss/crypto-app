package com.example.cryptoapp.ui.watchlist

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.cryptoapp.R
import android.graphics.*
import android.view.View

import kotlin.math.abs
import kotlin.math.min

class SwipeToRemoveCallback(
    context: Context,
    private val onRemove: (position: Int) -> Unit
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

    private val removePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.removeRed)
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, android.R.color.white)
        textSize = 48f
        isFakeBoldText = true
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    // όταν περάσει threshold θα καλεστεί το onSwiped
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        onRemove(viewHolder.bindingAdapterPosition)
    }

    //Αν αφήσει στη μέση και δεν περάσει threshold
    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return 0.70f
    }


    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        viewHolder.itemView.translationX = 0f
        recyclerView.invalidate()
    }

    // kokkino

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val itemView = viewHolder.itemView

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE && dX < 0) {

            val left = itemView.right + dX
            val right = itemView.right.toFloat()
            val top = itemView.top.toFloat()
            val bottom = itemView.bottom.toFloat()

            // κόκκινο background
            val rect = RectF(left, top, right, bottom)
            c.drawRoundRect(rect, 32f, 32f, removePaint)

            //to icon
            val icon = ContextCompat.getDrawable(recyclerView.context, R.drawable.ic_delete)!!


            val iconSize = 64
            val margin = 48

            val iconLeft = right.toInt() - margin - iconSize
            val iconTop = itemView.top + (itemView.height - iconSize) / 2

            icon.setBounds(
                iconLeft,
                iconTop,
                iconLeft + iconSize,
                iconTop + iconSize
            )


            icon.setTint(Color.WHITE)
            icon.draw(c)
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

}
