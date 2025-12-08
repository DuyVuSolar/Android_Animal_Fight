package com.kuemiin.animalfight.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class IntroSplash(
    val title: Int,
    val res: Int,
    val index: Int,
    val showNative : Boolean,
    val showNativeFull : Boolean = false,
    val isPerNotification : Boolean = false,
    val isPerMicrophone : Boolean = false,
) : BaseModel(), Parcelable {}
