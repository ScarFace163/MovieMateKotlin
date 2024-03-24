package com.example.moviemate.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.cardview.widget.CardView
import com.example.moviemate.R
import com.example.moviemate.SwipeListener


class FragmentCardChoosing : Fragment() {

    private lateinit var card : CardView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_card_choosing,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        card = view.findViewById(R.id.card)
        card.setOnTouchListener(SwipeListener(requireContext()))

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            // Вернуться на предыдущий фрагмент
            parentFragmentManager.popBackStack()
        }

    }




}