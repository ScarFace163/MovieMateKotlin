package com.example.moviemate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.cardview.widget.CardView
import com.example.moviemate.db.MainDatabase

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val card : CardView = findViewById(R.id.card)
        card.setOnTouchListener(SwipeListener(this))
        MainDatabase.testDatabaseConnection(this)
    }
}