package com.endumedia.herokick.vo

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


/**
 * Created by Nino on 01.10.19
 */
@Entity
data class Product(@PrimaryKey @SerializedName("gtin14") val id: String,
                   @SerializedName("brand_name")val brandName: String?,
                   val name: String?,
                   val size: String?)