package com.endumedia.herokick.vo

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


/**
 * Created by Nino on 01.10.19
 */
@Entity
data class Product(@PrimaryKey @SerializedName("gtin14") val id: String,
                   @SerializedName("brand_name") val brandName: String?,
                   val name: String?,
                   val size: String?,
                   @SerializedName("serving_size") val servingSize: String?,
                   @SerializedName("servings_per_container") val servingsPerContainer: String?,
                   @SerializedName("calories") val calories: String?,
                   @SerializedName("fat_calories") val fatCalories: String?,
                   val fat: String?, val sugars: String?, val protein: String?): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(brandName)
        parcel.writeString(name)
        parcel.writeString(size)
        parcel.writeString(servingSize)
        parcel.writeString(servingsPerContainer)
        parcel.writeString(calories)
        parcel.writeString(fatCalories)
        parcel.writeString(fat)
        parcel.writeString(sugars)
        parcel.writeString(protein)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Product> {
        override fun createFromParcel(parcel: Parcel): Product {
            return Product(parcel)
        }

        override fun newArray(size: Int): Array<Product?> {
            return arrayOfNulls(size)
        }
    }
}