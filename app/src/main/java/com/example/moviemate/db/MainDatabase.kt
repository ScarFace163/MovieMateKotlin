package com.example.moviemate.db

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.moviemate.MainActivity
import com.example.moviemate.entities.Movie
import com.example.moviemate.dao.Dao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Movie::class], version = 1)
abstract class MainDatabase  : RoomDatabase(){

    abstract fun getDao() : Dao
    companion object {
        @Volatile
        private var instance: MainDatabase? = null
        fun getDb(context: Context): MainDatabase {
            if (instance == null) {
                synchronized(MainDatabase::class) {
                    try {
                        instance = Room.databaseBuilder(
                            context,
                            MainDatabase::class.java,
                            "movieDatabase"
                        )
                            .createFromAsset("database/movieDatabase.db").build()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            return instance!!
        }


    }

}