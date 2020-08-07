package com.example.moviedatabaseappforedison


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.movie_item.view.*
import java.io.File


//Mostly boilerplate code
//I learned this from https://www.youtube.com/watch?v=afl_i6uvvU0&t=1150s
class  RecyclerAdapter( private val movieList: List<MovieObject>,
                        private val listener : OnItemClickListener,
                        private val directory: File
                            ) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.movie_item,
            parent, false)
        return ViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = movieList[position]
        holder.posterView.setImageBitmap(loadMovie(currentItem))
        holder.titleView.text = currentItem.title
        holder.genresView.text = currentItem.stringGenres
    }

    override fun getItemCount() = movieList.size

    private fun loadMovie(movie: MovieObject) : Bitmap{
        val newFile = File(directory , movie.id + ".png")
        return BitmapFactory.decodeFile(newFile.absolutePath)

    }
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
    View.OnClickListener{

        val posterView: ImageView = itemView.image_poster
        val titleView: TextView = itemView.title_view
        val genresView: TextView = itemView.genres_view

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }
    }
    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }


}