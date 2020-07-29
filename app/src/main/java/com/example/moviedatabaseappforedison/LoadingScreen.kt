package com.example.moviedatabaseappforedison

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


//Loading screen (splash screen) that is displayed while the movies objects load from the api.
class LoadingScreen : AppCompatActivity() {

    //numRequests representes how many api requests are needed to load all movies
    //      this is used to detemrine how full the progress bar should be
    private var numRequests = 0
    private val mainContext = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading_screen)

        //Coroutine needed to not freeze screen
        GlobalScope.launch{
              loadAllMovieObjects()
        }
    }

    //Loads popular movie list to movie list object
    private fun loadAllMovieObjects(){

        //Request URL *Note* this only loads the first page
        val url = "https://api.themoviedb.org/3/movie/popular?api_key=80df7863ef61abeeac17ee93f000216b"

        val queue = Volley.newRequestQueue(this)


        val stringRequest = StringRequest(
                    Request.Method.GET, url,
                    Response.Listener<String> { response ->
                            //Load Response to a movieList object using GSON
                            val movieList = Gson().fromJson(response, MovieList::class.java)
                            downloadAllMovies(movieList)

                    }, Response.ErrorListener {
                })
            queue.add(stringRequest)

    }
    //Downloads all movie images
     private fun downloadAllMovies(movieList: MovieList) {
        //ProgressBar shows progress of task, set max to how many movies need to be downloaded
         val progressBar = findViewById<ProgressBar>(R.id.progressBar)
         progressBar.max = movieList.results.size

         val queue = Volley.newRequestQueue(this)

         val movieListIterator = movieList.results.iterator()
         while(movieListIterator.hasNext()) {
             val movie = movieListIterator.next()
             val imageUrl = "https://image.tmdb.org/t/p/w500/" + movie.poster_path + "?api_key=80df7863ef61abeeac17ee93f000216b"

             val f = File(this.filesDir, movie.id + ".png")

             //Skip file load if it already is cached
             if (f.exists()) {
                 Log.d("Debug", "File exists")
                 if(!movieListIterator.hasNext()) {
                     val mainIntent = Intent(mainContext, MainActivity::class.java)
                     Log.e("Debug", "StartIntent")
                     startActivity(mainIntent)
                     mainContext.finish()
                 }

             }
             //If file isn't already cached download to a png file using a bitmap and update progress bar
             else {
                 Log.d("Debug", "File does not exist")
                 val imageRequest = ImageRequest(
                     imageUrl,
                     Response.Listener {

                         val bos = ByteArrayOutputStream()
                         it.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos)
                         val bitmapdata = bos.toByteArray()
                         val fos = FileOutputStream(f)
                         fos.write(bitmapdata)
                         fos.flush()
                         fos.close()
                         progressBar.progress = movieList.results.size - numRequests

                         numRequests--

                         //StartMain context if all movies are loaded
                         if (numRequests == 0) {
                             val mainIntent = Intent(mainContext, MainActivity::class.java)
                             Log.e("Debug", "StartIntent")
                             startActivity(mainIntent)
                             mainContext.finish()
                         }

                     },
                     200, 200,
                     ImageView.ScaleType.CENTER_CROP,
                     Bitmap.Config.ARGB_8888,
                     Response.ErrorListener {
                         println("Error Downloading Image")
                         Log.e("Error", " Error downloading image. Error: $it")
                     }
                 )

                 queue.add(imageRequest)
                 numRequests++
             }

         }
     }
}