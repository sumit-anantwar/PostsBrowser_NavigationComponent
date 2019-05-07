package com.sumitanantwar.presentation.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Post(
    val id: Int,
    val userId: Int,
    val title: String,
    val body: String
) : Parcelable