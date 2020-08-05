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
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.Serializable
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T



class MainActivity : AppCompatActivity() {

    private var genresMap : MutableMap<Int, String> = mutableMapOf(0 to "null")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val movieList = intent.getSerializableExtra("MovieList") as MovieList
        //Coroutine to avoid freezing main thread
        CoroutineScope(IO).launch{
           // test(movieList.results)
            for(movie in movieList.results ){
                //Remove last comma from string genres
                createMovieUIObject(movie)

            }
           // loadMovies()
        }
    }

    private fun test(movieList: List<MovieObject>){

        for(movie in movieList){
            Log.e("Debug" , "Movie: " + movie.title)
            Log.e( "debug", "genres: " +movie.stringGenres)
           // val bmp = BitmapFactory.decodeByteArray(movie.image_resource, 0, movie.image_resource.size)

            //this.testImageView.setImageBitmap(movie.image_resource)
        }
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

