package com.example.moviedatabaseappforedison
import android.content.Intent
import android.graphics.*

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.widget.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class MainActivity : AppCompatActivity() {

    private var genresMap : MutableMap<Int, String> = mutableMapOf(0 to "null")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Coroutine to avoid freezing main thread
        CoroutineScope(IO).launch{
           startMovieLoad()
        }
    }

     //Start Creating the UI. Being with loading genres
     private fun startMovieLoad(){
         val queue = Volley.newRequestQueue(this)
         val genreURL = "https://api.themoviedb.org/3/genre/movie/list?api_key=80df7863ef61abeeac17ee93f000216b"

         //API request to load all genres
         val genreRequest = StringRequest(
             Request.Method.GET, genreURL,
             Response.Listener<String> { response ->
                 //Load genre api response to our Genre List object
                 val genreList = Gson().fromJson(response, GenreList::class.java )
                 for(genre: GenreObject in genreList.genres){
                    // this.genresMap.put(genre.id, genre.name)
                     this.genresMap[genre.id] = genre.name
                 }
                 loadMovies()
             },
             Response.ErrorListener{
                 Log.e("Error", "Error downloading Genres")
             })
         queue.add(genreRequest)
     }

     private fun loadMovies(){

         val url = "https://api.themoviedb.org/3/movie/popular?api_key=80df7863ef61abeeac17ee93f000216b"
         val queue = Volley.newRequestQueue(this)

         val stringRequest = StringRequest(
             Request.Method.GET, url,
             Response.Listener<String> { response ->

                 val movieList = Gson().fromJson(response, MovieList::class.java )
                 for(movie in movieList.results ){
                     var genresString = ""

                     //Create genres string for movie object
                     for(genreID : Int in movie.genre_ids){
                        genresString = genresString + this.genresMap[genreID] + ", "
                     }

                     //Remove last comma from string genres
                     movie.stringGenres = genresString.substring(0, genresString.length -2)
                     CoroutineScope(IO).launch{
                         this@MainActivity.createMovieUIObject(movie)
                     }
                 }
             },
             Response.ErrorListener { })
         queue.add(stringRequest)
    }

    private suspend fun createMovieUIObject(movie: MovieObject){
        val linLayout = LinearLayout(this)
        linLayout.orientation = LinearLayout.HORIZONTAL

        val detailsLayout = LinearLayout(this)
        detailsLayout.orientation = LinearLayout.VERTICAL
        detailsLayout.setVerticalGravity(Gravity.CENTER_VERTICAL)
        val newFile = File(this.filesDir, movie.id + ".png")
        val myBitmap = BitmapFactory.decodeFile(newFile.absolutePath)
        val imageView = ImageView(this)
        imageView.setImageBitmap(myBitmap)

        val title = movie.title
        val titleView = TextView(this)
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP,60.toFloat())
        titleView.textAlignment = View.TEXT_ALIGNMENT_GRAVITY
        titleView.gravity = Gravity.CENTER_VERTICAL

        titleView.text = title
        titleView.setTypeface(null, Typeface.BOLD)

        val genres = movie.stringGenres
        val genresView = TextView(this)
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP,40.toFloat())
        genresView.text = genres

        linLayout.addView(imageView)
        detailsLayout.addView(titleView)
        detailsLayout.addView(genresView)
        linLayout.addView(detailsLayout)
        linLayout.setOnClickListener{
            onClick(movie)
        }
        addLinearLayout(linLayout)

    }
    private suspend fun addLinearLayout(linLayout: LinearLayout) {
        withContext (Main) {
            val parentLayout = findViewById<LinearLayout>(R.id.layout)
            parentLayout.addView(linLayout)
        }
    }
    private fun onClick(movie: MovieObject){

        //Start movie details class with info about which movie was clicked
        val intent =  Intent(this, MovieDetails::class.java)
        intent.putExtra("Title", movie.title)
        intent.putExtra("Overview", movie.overview)
        intent.putExtra("MovieGenres", movie.stringGenres)
        intent.putExtra("PosterPath", movie.poster_path)
        intent.putExtra("id", movie.id)
        intent.putExtra("voteAverage" , movie.vote_average)
        startActivity(intent)
    }
}

