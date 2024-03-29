package com.example.moviemate.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.moviemate.R
import java.util.LinkedList


class FragmentStartMenu : Fragment() {

    private lateinit var buttonStart:Button
    private lateinit var buttonList:Button
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_start_menu,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonStart = view.findViewById(R.id.buttonStart)
        buttonList = view.findViewById(R.id.buttonList)

        buttonStart.setOnClickListener {
            val cardChoosingFragment = FragmentCardChoosing()
            val transaction : FragmentTransaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.layoutForFragments,cardChoosingFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
        buttonList.setOnClickListener {
            val moviesListFragment = MoviesListFragment()
            val transaction : FragmentTransaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.layoutForFragments,moviesListFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }


}