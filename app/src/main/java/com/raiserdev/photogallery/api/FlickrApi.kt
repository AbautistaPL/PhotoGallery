package com.raiserdev.photogallery.api

import com.raiserdev.photogallery.model.FlickerResponse
import com.raiserdev.photogallery.model.GalleryItem
import retrofit2.http.GET
import retrofit2.http.Query

private const val API_KEY = "84e8d6117af6bd6558cd18c33bdd8f85"
interface FlickrApi {
    /*@GET("services/rest/?method=flickr.interestingness.getList" +
    "&api_key=$API_KEY" +
    "&format=json" +
    "&nojsoncallback=1" +
    "&extras=url_s")*/
    @GET("services/rest/?method=flickr.interestingness.getList")
    suspend fun fetchPhotos(): FlickerResponse

    @GET("services/rest?method=flickr.photos.search")
    suspend fun searchPhotos(@Query("text") query: String): FlickerResponse

}