package com.example.moviemate.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.moviemate.entities.Movie

@Dao
interface Dao {


    @Query("SELECT * FROM movieDatabase")
    fun getAll(): List<Movie>

    @Query("SELECT * FROM movieDatabase WHERE id = :moveId")
    fun getById(moveId: Int): Movie?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(movie: Movie)
    fun getNextCard(): Movie{
        var rand : Int = (1 until 21).random()
        val movie = getById(rand)
        return movie ?: getNextCard()
    }
}