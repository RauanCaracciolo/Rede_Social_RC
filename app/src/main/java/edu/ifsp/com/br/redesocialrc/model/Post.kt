package edu.ifsp.com.br.redesocialrc.model

import android.graphics.Bitmap

data class Post(
    val id: String,
    val userProfilePhoto: Bitmap?,
    val userName: String,
    val title: String,
    val image: Bitmap?
)