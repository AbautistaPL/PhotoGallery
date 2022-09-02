package com.raiserdev.photogallery.api

import retrofit2.http.GET
private const val API_KEY = "your_api_key"
interface FlickrApi {
    @GET("services/rest/?method=flickr.interestingness.getList" +
    "&api_key=$API_KEY" +
    "&format=json" +
    "&nojsoncallback=1" +
    "&extrass=url_s")
    suspend fun fetchContents(): String
}