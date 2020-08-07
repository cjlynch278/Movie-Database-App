package com.example.moviedatabaseappforedison

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), RecyclerAdapter.OnItemClickListener {

    private var movieResult: MovieList? = null
    private var movieList: List<MovieObject>? = null
    private var adapter: RecyclerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        movieResult = intent.getSerializableExtra("MovieList") as MovieList
        movieList = movieResult?.results
        adapter = RecyclerAdapter(movieList!!, this, this.filesDir)
        movieList = movieResult?.results
        recycle_view.adapter = adapter
        recycle_view.layoutManager = LinearLayoutManager(this)
        recycle_view.setHasFixedSize(true)

    }

    override fun onItemClick(position: Int) {
        val movieList = movieResult?.results
        val clickedItem = movieList!![position]
        Log.e("Debug", "You've clicked " + clickedItem.title)

        val intent = Intent(this, MovieDetails::class.java)
        intent.putExtra("Title", clickedItem.title)
        intent.putExtra("Overview", clickedItem.overview)
        intent.putExtra("MovieGenres", clickedItem.stringGenres)
        intent.putExtra("PosterPath", clickedItem.poster_path)
        intent.putExtra("id", clickedItem.id)
        intent.putExtra("voteAverage", clickedItem.vote_average)
        startActivity(intent)
        adapter?.notifyItemChanged(position)

    }
}

