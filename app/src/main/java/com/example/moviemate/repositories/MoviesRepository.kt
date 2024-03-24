package com.example.moviemate.repositories

import com.example.moviemate.dao.Dao
import com.example.moviemate.entities.Movie


class MoviesRepository(private val dao: Dao) {

    fun getMovies() : List<Movie>{
        return dao.getAll()
    }
    fun isertMovie(movie: Movie){
        dao.insert(movie)
    }

}