package model

// Representa la dirección postal del usuario
data class Direccion(
    val calle: String,
    val numero: String,
    val puerta: String,
    val cp: String,
    val ciudad: String
)