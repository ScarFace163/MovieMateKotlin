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
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.moviemate.db.MainDatabase
import kotlin.math.abs
import com.example.moviemate.entities.Movie
import com.example.moviemate.fragments.FragmentRoundSwap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.LinkedList
import java.util.Queue

class SwipeListener(private val context: Context, private val fragment: Fragment, private val isSecondRound: Boolean, private val q: Queue<Movie> = LinkedList<Movie>()): View.OnTouchListener {

    var queueOfUsedMoviesById:Queue<Movie> = q
    var queueOfChoosedMoviesAfterFirstRound:Queue<Movie> = LinkedList<Movie>()
    var queueOfResults:Queue<Movie> = LinkedList<Movie>()
    private val db = MainDatabase.getDb(context)
    private var downX: Float = 0f
    private var downY: Float = 0f
    private var swiped: Boolean = false
    private var secondRound : Boolean = isSecondRound

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
                spawnNewCard(v)
                resetCardPosition(v)
            }
        })
        if (!secondRound) {
            if (!queueOfUsedMoviesById.isEmpty()) queueOfChoosedMoviesAfterFirstRound.add(queueOfUsedMoviesById.peek())
        }
        else{
            val movieCheck = queueOfUsedMoviesById.peek()
            if (movieCheck != null) {
                Log.d("Movie", movieCheck.title)
            }
            if (queueOfChoosedMoviesAfterFirstRound.contains(movieCheck)){
                queueOfResults.add(movieCheck)
            }
        }


        Log.d("Queue of choosed after first", queueOfChoosedMoviesAfterFirstRound.toString())
        Log.d("is SecondRound", secondRound.toString())
        Log.d("Queue of results", queueOfResults.toString())

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

    private fun spawnNewCard(v: View ) {
        CoroutineScope(Dispatchers.Main).launch {
            val newCardData : Movie
            if (secondRound) {

                newCardData =queueOfUsedMoviesById.poll()
            }
            else {
                newCardData = getNewCardData(v)
            }
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
    private suspend fun getNewCardData(v : View ) : Movie  = withContext(Dispatchers.IO){
        var element:Movie
        do {
            element = db.getDao().getNextCard()
        } while (queueOfUsedMoviesById.contains(element))
        queueOfUsedMoviesById.add(element)
        if (queueOfUsedMoviesById.size == 10){
            withContext(Dispatchers.Main){
                val roundSwapFragment = FragmentRoundSwap(queueOfUsedMoviesById)
                val transaction : FragmentTransaction = fragment.parentFragmentManager.beginTransaction()
                transaction.replace(R.id.layoutForFragments, roundSwapFragment)
                transaction.addToBackStack(null)
                transaction.commit()
            }
        }

        return@withContext element
    }


    companion object {
        private const val SWIPE_THRESHOLD = 100
    }

}