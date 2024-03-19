package com.example.moviemate

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import com.example.moviemate.db.MainDatabase
import kotlin.math.abs
import com.example.moviemate.entities.Movie
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
    }

    private fun onSwipeLeft(v: View) {
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
    }

    private fun spawnNewCard(v: View) {
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
        title.text = newCardData.title
        year.text = newCardData.year.toString()
        description.text = newCardData.description


    }

    private fun resetCardPosition(v: View){
        val animator = ObjectAnimator.ofFloat(v, "translationX", 0f)
        animator.interpolator = DecelerateInterpolator()
        animator.duration = 100
        animator.start()
    }
    private fun getNewCardData() : Movie {
        val element = db.getDao().getNextCard()
        if (setOfUsedMoviesById.contains(element.id)){
            return getNewCardData()
        }
        if (setOfUsedMoviesById.size > 19){
            setOfUsedMoviesById.clear()
        }
        setOfUsedMoviesById.add(element.id)
        return element
    }


    companion object {
        private const val SWIPE_THRESHOLD = 100
    }

}