package com.example.randominsect.data.model

import java.util.UUID

data class Insect(
    val id: String = UUID.randomUUID().toString(),
    val nombreCientifico: String,
    val nombreComun: String,
    val orden: String,
    val habitat: String,
    val imageUri: String? = null
)
