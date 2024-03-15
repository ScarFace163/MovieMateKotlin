package com.example.moviemate

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
public final data class Movie(
    @PrimaryKey
    val id : Int,
    val title: String,
    val year : Int ,
    val description : String ,
    val categories: String
)