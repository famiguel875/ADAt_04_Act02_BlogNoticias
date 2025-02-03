package service

import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import org.bson.Document
import utils.ConexionMongo

class ComentarioService(database: MongoDatabase) {
    private val databaseName = "noticiasdb"
    // Se obtienen las colecciones "usuarios" y "comentarios" de la base de datos
    private val usuariosCollection: MongoCollection<Document> = ConexionMongo.getCollection(databaseName, "collUsuarios")
    private val comentariosCollection: MongoCollection<Document> = ConexionMongo.getCollection(databaseName, "collComentarios")

    /**
     * Permite escribir un comentario en una noticia.
     * Solo permite comentar si el usuario existe y está en estado "ACTIVO".
     */
    fun escribirComentario(usuarioIdentificador: String, comentario: Document): String {
        val filtro = Filters.or(
            Filters.eq("email", usuarioIdentificador),
            Filters.eq("nombreUsuario", usuarioIdentificador)
        )
        val usuarioDoc = usuariosCollection.find(filtro).first() ?: return "Error: Usuario no registrado."
        if (usuarioDoc.getString("estado") != "ACTIVO") {
            return "Error: El usuario no puede comentar porque está inactivo o baneado."
        }
        comentariosCollection.insertOne(comentario)
        return "Comentario añadido correctamente."
    }

    /**
     * Lista los comentarios asociados a una noticia determinada.
     */
    fun listarComentariosPorNoticia(noticiaId: String): List<Document> {
        return comentariosCollection.find(Filters.eq("noticiaId", noticiaId)).toList()
    }
}