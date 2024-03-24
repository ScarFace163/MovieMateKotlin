package com.example.moviemate.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moviemate.R
import com.example.moviemate.adapters.MoviesListAdapter
import com.example.moviemate.db.MainDatabase
import com.example.moviemate.entities.Movie
import com.example.moviemate.repositories.MoviesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MoviesListFragment : Fragment() {
    private lateinit var itemList:RecyclerView
    private lateinit var moviesRepository: MoviesRepository
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_movies_list,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        itemList = view.findViewById(R.id.itemList)
        val dao = MainDatabase.getDb(requireContext()).getDao()
        moviesRepository = MoviesRepository(dao)

        lifecycleScope.launch(Dispatchers.IO) {
            val movies: List<Movie> = moviesRepository.getMovies()
            withContext(Dispatchers.Main){
                itemList.layoutManager = LinearLayoutManager(requireContext())
                itemList.adapter = MoviesListAdapter(movies, requireContext())
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            // Вернуться на предыдущий фрагмент
            parentFragmentManager.popBackStack()
        }


    }
}