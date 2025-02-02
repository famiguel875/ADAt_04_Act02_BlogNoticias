package model

// Representa un usuario registrado en la plataforma
data class Usuario(
    val email: String,               // Actúa como _id_
    val nombreCompleto: String,
    val nombreUsuario: String,       // Debe ser único
    val estado: EstadoUsuario,
    val direccionPostal: Direccion,
    val telefonos: List<String>
)