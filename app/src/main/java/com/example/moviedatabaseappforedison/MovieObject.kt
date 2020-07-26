package com.example.moviedatabaseappforedison;


//Movie object to store info about each movie
data class MovieObject(
    val popularity: Double,
    val vote_count: Int,
    val video: Boolean,
    val poster_path: String,
    var id: String,
    val adult: Boolean,
    val backdrop_path: String,
    val original_language: String,
    val original_title: String,
    val genre_ids: Array<Int>,
    val title: String,
    val vote_average: Float,
    val overview: String,
    val release_date: String,
    val imageUrl: String,
    var stringGenres : String
) {


}
