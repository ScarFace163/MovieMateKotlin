package com.example.moviemate

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import com.example.moviemate.db.MainDatabase
import kotlin.math.abs
import com.example.moviemate.entities.Movie
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.HashSet

class SwipeListener (private val context: Context): View.OnTouchListener {

    var setOfUsedMoviesById = HashSet<Int>()
    private val db = MainDatabase.getDb(context)
    private var downX: Float = 0f
    private var downY: Float = 0f
    private var swiped: Boolean = false

    override fun onTouch(v: View, event: MotionEvent ): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.x
                downY = event.y
                swiped = false
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = event.x - downX
                val dy = event.y - downY
                if (abs(dx) > abs(dy) && abs(dx) > SWIPE_THRESHOLD) {
                    if (dx > 0) {
                        onSwipeRight(v)
                    } else {
                        onSwipeLeft(v)
                    }
                    swiped = true
                }
            }
            MotionEvent.ACTION_UP -> {
                if (!swiped) {
                    v.performClick()
                }
            }
        }
        return true
    }

    private fun onSwipeRight(v: View) {
        if (swiped) return

        val animator = ObjectAnimator.ofFloat(v, "translationX", v.width.toFloat() + 60f)
        animator.interpolator = DecelerateInterpolator()
        animator.duration = 300
        animator.start()
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                v.visibility = View.GONE
                resetCardPosition(v)
                spawnNewCard(v)
            }
        })
        swiped = true
    }

    private fun onSwipeLeft(v: View) {
        if (swiped) return

        val animator = ObjectAnimator.ofFloat(v, "translationX", -v.width.toFloat() - 60f)
        animator.interpolator = DecelerateInterpolator()
        animator.duration = 300
        animator.start()
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                v.visibility = View.GONE
                spawnNewCard(v)
                resetCardPosition(v)
            }
        })
        swiped = true
    }

    private fun spawnNewCard(v: View) {
        CoroutineScope(Dispatchers.Main).launch {
            val newCardData = getNewCardData()
            v.apply {
                visibility = View.VISIBLE
                alpha = 0f
                animate()
                    .setDuration(600)
                    .setListener(null)
                    .alpha(1f)
            }
            val title = v.findViewById<TextView>(R.id.cardTitle)
            val year = v.findViewById<TextView>(R.id.cardYear)
            val description = v.findViewById<TextView>(R.id.cardDescription)
            val image = v.findViewById<ImageView>(R.id.cardImage)
            val imageAddress:String ="@drawable/" + newCardData.image
            val imageId = context.resources.getIdentifier(
                imageAddress,
                "drawable",
                context.packageName)
            Log.d("Image Download" , imageAddress)
            image.setImageResource(imageId)
            title.text = newCardData.title
            year.text = newCardData.year.toString()
            description.text = newCardData.description
        }

    }

    private fun resetCardPosition(v: View){
        val animator = ObjectAnimator.ofFloat(v, "translationX", 0f)
        animator.interpolator = DecelerateInterpolator()
        animator.duration = 100
        animator.start()
    }
    private suspend fun getNewCardData() : Movie  = withContext(Dispatchers.IO){
        var element:Movie
        do {
            element = db.getDao().getNextCard()
        } while (setOfUsedMoviesById.contains(element.id))

        if (setOfUsedMoviesById.size > 10){
            setOfUsedMoviesById.clear()
        }
        setOfUsedMoviesById.add(element.id)
        return@withContext element
    }


    companion object {
        private const val SWIPE_THRESHOLD = 100
    }

}