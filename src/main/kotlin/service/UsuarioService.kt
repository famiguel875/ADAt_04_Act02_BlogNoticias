package service

import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import org.bson.Document
import utils.ConexionMongo
import org.bson.conversions.Bson

class UsuarioService(database: MongoDatabase) {
    private val databaseName = "noticiasdb"
    // Se obtiene la colección "usuarios" de la base de datos
    private val usuariosCollection: MongoCollection<Document> =
        ConexionMongo.getCollection(databaseName, "collUsuarios")

    /**
     * Registra un usuario verificando que el email y el nombre de usuario sean únicos.
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

    /**
     * Permite obtener un usuario de la colección a partir de un filtro.
     */
    fun getUsuario(filtro: Bson): Document? {
        return usuariosCollection.find(filtro).first()
    }
}