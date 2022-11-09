package com.raiserdev.photogallery.model

import android.net.Uri
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GalleryItem(
    @Json(name = "title")val title: String,
    @Json(name = "id")val id: String,
    @Json(name = "url_s")val url: String?,
    val owner: String
) {
    val photoPageUri: Uri
        get() = Uri.parse("https://www.flickr.com/photos/")
            .buildUpon()
            .appendPath(owner)
            .appendPath(id)
            .build()
    override fun toString(): String {
        return "GalleryItem(title='$title', id='$id', url='$url')"
    }
}

