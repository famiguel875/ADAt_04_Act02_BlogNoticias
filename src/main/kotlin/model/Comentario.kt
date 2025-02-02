package model

import java.util.*

// Representa un comentario realizado en una noticia
data class Comentario(
    val autor: String,
    val noticiaId: String,      // Puede ser el título o un identificador de la noticia
    val comentario: String,
    val fechaHora: Date
)