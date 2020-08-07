package com.example.moviedatabaseappforedison

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_movie_details.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.text.DecimalFormat

//Class for the movie details page
class MovieDetails : AppCompatActivity() {

    private var movieTitle: String? = null
    private var movieOverview: String? = null
    private var movieGenres: String? = null
    private var posterPath: String? = null
    private var id: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_details)

        this.movieTitle = intent.getStringExtra("Title")
        this.movieOverview = intent.getStringExtra("Overview")
        this.movieGenres = intent.getStringExtra("MovieGenres")
        this.posterPath = intent.getStringExtra("PosterPath")
        this.id = intent.getStringExtra("id")
        val decimalFormat = DecimalFormat("#.#")
        val movieVoteAverage = intent.getDoubleExtra("voteAverage", 0.toDouble())
        decimalFormat.format(movieVoteAverage)
        overview.text = this.movieOverview
        genres.text = this.movieGenres
        movietitle.text = this.movieTitle

        voteAverage.text = resources.getString(R.string.rating_text, movieVoteAverage)
        CoroutineScope(Dispatchers.IO).launch {
            loadImage()
        }

    }

    private fun loadImage() {
        val newFile = File(this.filesDir, this.id.toString() + ".png")
        val myBitmap = BitmapFactory.decodeFile(newFile.absolutePath)
        poster.setImageBitmap(myBitmap)
    }

    @Override
    fun backClick() {
        finish()
    }


}
