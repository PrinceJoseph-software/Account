package com.example.account

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Entity (
    @SerialName("id") val id: String = "", // Changed to String for UUID
    @SerialName("firstname") val firstname: String = "",
    @SerialName("lastname") val lastname: String = "",
    @SerialName("email") val email: String = "",
    @SerialName("phonenumber") val phonenumber: String = "" // Changed to String
    // Removed password - it's stored in Supabase Auth, not your table
)