package model

import java.text.SimpleDateFormat
import java.util.*

// Representa una noticia publicada en el blog
data class Noticia(
    val titulo: String,
    val cuerpo: String,
    val fechaPublicacion: Date, // Única y no editable
    val autor: String,          // Puede ser el nombre de usuario o email
    val tags: List<String>? = null
) {
    override fun toString(): String {
        val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
        return "Título: $titulo\n" +
                "Cuerpo: $cuerpo\n" +
                "Fecha de Publicación: ${sdf.format(fechaPublicacion)}\n" +
                "Autor: $autor\n" +
                "Tags: ${tags?.joinToString(", ") ?: "Sin etiquetas"}\n"
    }
}