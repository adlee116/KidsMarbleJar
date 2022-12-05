package com.example.kidsmarblejar.presentation.home

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity @kotlinx.parcelize.Parcelize
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "user_name")
    val name: String,

    @ColumnInfo(name = "user_image", typeAffinity = ColumnInfo.BLOB)
    val image: ByteArray?,

    val marbles: Int,

    val conversionType: Int,

    val conversionValue: Int,

    @ColumnInfo(name = "requires_password")
    val requiresPassword: Boolean,

    val goal: Int,

    @ColumnInfo(name = "goal_name")
    val goalName: String,

    @ColumnInfo(name = "is_adult")
    val isAdult: Boolean
): Parcelable

