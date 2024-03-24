package com.example.moviemate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentTransaction
import com.example.moviemate.db.MainDatabase
import com.example.moviemate.fragments.FragmentStartMenu

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val startMenuFragment = FragmentStartMenu()
        val transaction : FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.layoutForFragments,startMenuFragment)
        transaction.commit()

    }
}