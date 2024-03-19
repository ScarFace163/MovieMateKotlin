package com.example.moviemate.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "movieDatabase")
public final data class Movie(
    @PrimaryKey (autoGenerate = true)
    val id : Int,
    val title: String,
    val year : Int,
    val description : String ,
    val categories: String,
    val image : String)