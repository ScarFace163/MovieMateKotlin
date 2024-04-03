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
import com.example.moviemate.fragments.FragmentCardChoosing
import com.example.moviemate.fragments.FragmentChoosedMovies
import com.example.moviemate.fragments.FragmentRoundSwap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.LinkedList
import java.util.Queue

class SwipeListener(private val context: Context, private val fragment: Fragment): View.OnTouchListener {

    var queueOfUsedMoviesById:ArrayDeque<Movie> = ArrayDeque<Movie>()
    var queueOfChoosedMoviesAfterFirstRound: Queue<Movie> = LinkedList<Movie>()
    var listOfResults:MutableList<Movie> = ArrayList<Movie>()
    private val db = MainDatabase.getDb(context)
    private var downX: Float = 0f
    private var downY: Float = 0f
    private var swiped: Boolean = false
    private var swipedLeft: Boolean = false
    private var secondRound : Boolean = false

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
        swipedLeft = false
        swiped = true
        val movieCheck: Movie
        if (secondRound){
            try{
                movieCheck = queueOfUsedMoviesById.last()
                if (queueOfChoosedMoviesAfterFirstRound.contains(movieCheck)){
                    listOfResults.add(movieCheck)
                }
            }catch(e: NoSuchElementException){
                switchToFragmentChoosedMovies()
            }

        }
        else{
            if (!queueOfUsedMoviesById.isEmpty()) {
                movieCheck = queueOfUsedMoviesById.last()
                queueOfChoosedMoviesAfterFirstRound.add(queueOfUsedMoviesById.last())
            }
        }
        if (secondRound){
            queueOfUsedMoviesById.removeLast()
        }
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



    }

    private fun onSwipeLeft(v: View) {
        if (swiped) return
        swipedLeft = true
        swiped=true
        if (secondRound){
            queueOfUsedMoviesById.removeLast()
        }
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

        Log.d("second" , secondRound.toString())
    }

    private fun spawnNewCard(v: View ) {
        CoroutineScope(Dispatchers.Main).launch {
            var newCardData : Movie
            if (secondRound) {
                try{
                    newCardData = queueOfUsedMoviesById.last()
                } catch (e: NoSuchElementException){
                    switchToFragmentChoosedMovies()
                    newCardData = Movie(0, "",0,"","", "")
                }
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

        if (queueOfUsedMoviesById.size ==15 ){
            withContext(Dispatchers.Main){
                val roundSwapFragment = FragmentRoundSwap()
                secondRound = true
                roundSwapFragment.show(fragment.parentFragmentManager, "roundSwap")
                element = queueOfUsedMoviesById.last()
                return@withContext element
            }
        }else{
            queueOfUsedMoviesById.add(element)
        }

        return@withContext element
    }
    private fun switchToFragmentChoosedMovies() {
        val fragmentChoosedMovies = FragmentChoosedMovies(listOfResults)
        val transaction: FragmentTransaction = fragment.parentFragmentManager.beginTransaction()
        transaction.replace(R.id.layoutForFragments, fragmentChoosedMovies)
        transaction.addToBackStack(null)
        transaction.commit()
    }


    companion object {
        private const val SWIPE_THRESHOLD = 100
    }

}