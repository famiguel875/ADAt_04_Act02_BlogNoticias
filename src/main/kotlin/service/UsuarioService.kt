package service

import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import org.bson.Document
import utils.ConexionMongo

class UsuarioService(database: MongoDatabase) {
    private val databaseName = "noticiasdb"
    // Se obtiene la colección "usuarios" de la base de datos
    private val usuariosCollection: MongoCollection<Document> = ConexionMongo.getCollection(databaseName, "collUsuarios")

    /**
     * Registra un usuario verificando que el email y el nombre de usuario sean únicos.
     * @param usuario Documento con los datos del usuario.
     * @return Mensaje indicando el resultado de la operación.
     */
    fun registrarUsuario(usuario: Document): String {
        val filtro = Filters.or(
            Filters.eq("email", usuario.getString("email")),
            Filters.eq("nombreUsuario", usuario.getString("nombreUsuario"))
        )
        if (usuariosCollection.find(filtro).first() != null) {
            return "Error: El email o nombre de usuario ya existe."
        }
        usuariosCollection.insertOne(usuario)
        return "Usuario registrado correctamente."
    }
}