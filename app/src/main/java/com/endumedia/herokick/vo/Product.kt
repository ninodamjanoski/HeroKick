package com.endumedia.herokick.vo

import com.google.gson.annotations.SerializedName


/**
 * Created by Nino on 01.10.19
 */
data class Product(val gtin14: String,
                   @SerializedName("brand_name")val brandName: String,
                   val name: String,
                   val size: String)