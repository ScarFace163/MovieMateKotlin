package com.example.moviemate.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.moviemate.R
import com.example.moviemate.entities.Movie
import java.util.Queue


class FragmentRoundSwap(val q : Queue<Movie>) : Fragment() {
    private lateinit var button: Button
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_round_swap,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val gifImageView = view.findViewById<ImageView>(R.id.myGif)

        Glide.with(this)
            .load(R.drawable.arrow_gif)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(gifImageView)

        button = view.findViewById(R.id.buttonStartRound)

        button.setOnClickListener {
            val cardChoosingFragment = FragmentCardChoosing(true, q)
            val transaction : FragmentTransaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.layoutForFragments,cardChoosingFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }




}