package com.raiserdev.photogallery.api

import com.raiserdev.photogallery.model.FlickerResponse
import retrofit2.http.GET
private const val API_KEY = "84e8d6117af6bd6558cd18c33bdd8f85"
interface FlickrApi {
    @GET("services/rest/?method=flickr.interestingness.getList" +
    "&api_key=$API_KEY" +
    "&format=json" +
    "&nojsoncallback=1" +
    "&extrass=url_s")
    suspend fun fetchPhotos(): FlickerResponse
}