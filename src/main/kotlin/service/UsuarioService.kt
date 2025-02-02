package service

import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import model.Usuario

class UsuarioService(database: MongoDatabase) {
    private val collection: MongoCollection<Usuario> =
        database.getCollection("usuarios", Usuario::class.java)

    // Registra un nuevo usuario si no existe otro con el mismo email o nombre de usuario
    fun registrarUsuario(usuario: Usuario): String {
        val filtro = Filters.or(
            Filters.eq("email", usuario.email),
            Filters.eq("nombreUsuario", usuario.nombreUsuario)
        )
        if (collection.find(filtro).first() != null) {
            return "Error: El email o nombre de usuario ya existe."
        }
        collection.insertOne(usuario)
        return "Usuario registrado correctamente."
    }

    // Inicia sesión buscando por email o nombre de usuario
    fun iniciarSesion(identificador: String): Usuario? {
        val filtro = Filters.or(
            Filters.eq("email", identificador),
            Filters.eq("nombreUsuario", identificador)
        )
        return collection.find(filtro).first()
    }
}