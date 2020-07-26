package com.example.moviedatabaseappforedison

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.activity_movie_details.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

//Class for the movie details page
class MovieDetails : AppCompatActivity() {

    var movieTitle : String? = null
    var movieOverview : String? = null
    var movieGenres : String? = null
    var posterPath : String? = null
    var id : String? = null
    var movieVoteAverage: Float? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_details)

        this.movieTitle = intent.getStringExtra("Title")
        this.movieOverview = intent.getStringExtra("Overview")
        this.movieGenres = intent.getStringExtra("MovieGenres")
        this.posterPath = intent.getStringExtra("PosterPath")
        this.id = intent.getStringExtra("id")
        this.movieVoteAverage = intent.getFloatExtra("voteAverage", 0.toFloat())
        overview.text = this.movieOverview
        genres.text = this.movieGenres
        movietitle.text = this.movieTitle
        voteAverage.text = "Rating: " + movieVoteAverage + "/10"
        createUI()

    }
    fun createUI(){
        var context =this
        CoroutineScope(Dispatchers.IO).launch{
            loadImage()
        }
    }
    suspend fun  loadImage() {
        val newFile = File(this.filesDir, this.id.toString() + ".png")
        val myBitmap = BitmapFactory.decodeFile(newFile.absolutePath)
        poster.setImageBitmap(myBitmap)
    }

    private suspend fun addLinearLayout(linLayout: LinearLayout) {
        withContext (Dispatchers.Main) {
            val parentLayout = findViewById<LinearLayout>(R.id.layout)
            parentLayout.addView(linLayout)

        }
    }



}
