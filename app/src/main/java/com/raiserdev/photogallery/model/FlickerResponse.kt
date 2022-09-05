package com.raiserdev.photogallery.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class FlickerResponse(
    val photos: PhotoResponse
)