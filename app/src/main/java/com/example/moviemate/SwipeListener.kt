package com.example.moviemate

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import kotlin.math.abs

class SwipeListener : View.OnTouchListener {

    private var downX: Float = 0f
    private var downY: Float = 0f
    private var swiped: Boolean = false

    override fun onTouch(v: View, event: MotionEvent): Boolean {
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
        //val newCardData = getNewCardData()
        v.apply {
            // Здесь вы можете установить новые данные для вашей карточки
            // Например, если у вас есть TextView внутри карточки, вы можете установить новый текст так:
            // findViewById<TextView>(R.id.card_text).text = newCardData
            visibility = View.VISIBLE // делаем карточку видимой
            alpha = 0f // делаем карточку полностью прозрачной
            animate()
                // делаем карточку полностью непрозрачной
                .setDuration(600)
                .setListener(null)
                .alpha(1f)
        }
    }

    private fun resetCardPosition(v: View){
        val animator = ObjectAnimator.ofFloat(v, "translationX", 0f)
        animator.interpolator = DecelerateInterpolator()
        animator.duration = 100
        animator.start()
    }
    companion object {
        private const val SWIPE_THRESHOLD = 100
    }

}