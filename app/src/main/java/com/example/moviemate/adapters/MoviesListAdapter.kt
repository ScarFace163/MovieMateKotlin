package com.example.moviemate.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.moviemate.R
import com.example.moviemate.entities.Movie

class MoviesListAdapter(var movies : List<Movie>, var context : Context) : RecyclerView.Adapter<MoviesListAdapter.ViewHolder>(){
    class ViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val title: TextView = view.findViewById(R.id.cardTitle)
        val description : TextView = view.findViewById(R.id.cardDescription)
        val image : ImageView = view.findViewById(R.id.cardImage)
        val year: TextView = view.findViewById(R.id.cardYear)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view  = LayoutInflater.from(parent.context).inflate(R.layout.item_in_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return movies.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.text = movies[position].title
        holder.description.text = movies[position].description


        val imageId = context.resources.getIdentifier(
            movies[position].image,
            "drawable",
            context.packageName)
        holder.image.setImageResource(imageId)

    }
}