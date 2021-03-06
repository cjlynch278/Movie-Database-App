package com.example.moviedatabaseappforedison

import java.io.Serializable

//Data class to store movie api response
data class MovieList(
    val page: Int,
    val total_results: Int,
    val total_pages: Int,
    val results: List<MovieObject>
) : Serializable {

    public fun getMovieList(): List<MovieObject> {
        return results
    }
}