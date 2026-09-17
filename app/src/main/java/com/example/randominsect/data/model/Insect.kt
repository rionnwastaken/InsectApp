package com.example.randominsect.data.model

import java.util.UUID
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "insects")
data class Insect(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val nombreCientifico: String,
    val nombreComun: String,
    val orden: String,
    val habitat: String,
    val imageUri: String? = null
)
