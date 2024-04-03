package com.example.moviemate.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.addCallback
import androidx.fragment.app.FragmentTransaction
import com.example.moviemate.R
import com.example.moviemate.SwipeListener


class FragmenRoundStart : Fragment() {
    private lateinit var  button : Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_fragmen_round_start,container,false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        button = view.findViewById(R.id.buttonStartFirstRound)
        button.setOnClickListener {
            val cardChoosingFragment = FragmentCardChoosing()
            val transaction : FragmentTransaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.layoutForFragments,cardChoosingFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            // Вернуться на предыдущий фрагмент
            parentFragmentManager.popBackStack()
        }
    }


}