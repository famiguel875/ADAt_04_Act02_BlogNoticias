import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Sorts
import org.bson.Document
import utils.ConexionMongo
import java.util.Date

fun main() {
    // Nombre de la base de datos
    val databaseName = "noticiasdb"
    // Obtención de las colecciones usando ConexionMongo
    val usuariosCollection: MongoCollection<Document> = ConexionMongo.getCollection(databaseName, "collUsuarios")
    val noticiasCollection: MongoCollection<Document> = ConexionMongo.getCollection(databaseName, "collNoticias")
    val comentariosCollection: MongoCollection<Document> = ConexionMongo.getCollection(databaseName, "collComentarios")

    try {
        while (true) {
            println("\n--- Menú de Gestión del Blog de Noticias ---")
            println("1. Registrar usuario")
            println("2. Publicar noticia")
            println("3. Listar noticias por usuario")
            println("4. Escribir comentario")
            println("5. Listar comentarios de una noticia")
            println("6. Buscar noticias por etiqueta")
            println("7. Listar las 10 últimas noticias")
            println("8. Salir")
            print("Selecciona una opción: ")

            when (readln().toInt()) {
                1 -> {
                    // Registrar usuario
                    println("\n-- Registro de Usuario --")
                    print("Email: ")
                    val email = readln()
                    print("Nombre completo: ")
                    val nombreCompleto = readln()
                    print("Nombre de usuario: ")
                    val nombreUsuario = readln()
                    print("Estado (ACTIVO, INACTIVO, BANNED): ")
                    val estado = readln()
                    println("Dirección postal:")
                    print("  Calle: ")
                    val calle = readln()
                    print("  Número: ")
                    val numero = readln()
                    print("  Puerta: ")
                    val puerta = readln()
                    print("  Código postal: ")
                    val cp = readln()
                    print("  Ciudad: ")
                    val ciudad = readln()
                    print("Teléfonos (separados por coma): ")
                    val telefonos = readln().split(",").map { it.trim() }

                    val direccion = Document("calle", calle)
                        .append("numero", numero)
                        .append("puerta", puerta)
                        .append("cp", cp)
                        .append("ciudad", ciudad)

                    val usuario = Document("email", email)
                        .append("nombreCompleto", nombreCompleto)
                        .append("nombreUsuario", nombreUsuario)
                        .append("estado", estado)
                        .append("direccionPostal", direccion)
                        .append("telefonos", telefonos)

                    println(registrarUsuario(usuariosCollection, usuario))
                }
                2 -> {
                    // Publicar noticia
                    println("\n-- Publicar Noticia --")
                    print("Título: ")
                    val titulo = readln()
                    print("Cuerpo: ")
                    val cuerpo = readln()
                    print("Autor (nombre de usuario): ")
                    val autor = readln()
                    print("Etiquetas (separadas por coma, opcional): ")
                    val tagsInput = readln()
                    val tags = if (tagsInput.isBlank()) listOf<String>()
                    else tagsInput.split(",").map { it.trim() }
                    // Se utiliza la fecha actual para la publicación
                    val fechaPublicacion = Date()

                    val noticia = Document("titulo", titulo)
                        .append("cuerpo", cuerpo)
                        .append("fechaPublicacion", fechaPublicacion)
                        .append("autor", autor)
                        .append("tags", tags)
                    println(publicarNoticia(noticiasCollection, noticia))
                }
                3 -> {
                    // Listar noticias por usuario
                    println("\n-- Listar Noticias por Usuario --")
                    print("Ingrese el nombre de usuario: ")
                    val autor = readln()
                    val noticias = listarNoticiasPorUsuario(noticiasCollection, autor)
                    if (noticias.isEmpty()) {
                        println("No se encontraron noticias para el usuario $autor")
                    } else {
                        noticias.forEach { println(it.toJson()) }
                    }
                }
                4 -> {
                    // Escribir comentario
                    println("\n-- Escribir Comentario --")
                    print("Ingrese su identificador (email o nombre de usuario): ")
                    val usuarioIdentificador = readln()
                    print("Título de la noticia en la que comentar: ")
                    val noticiaId = readln()
                    print("Escriba su comentario: ")
                    val comentarioTexto = readln()
                    val comentario = Document("autor", usuarioIdentificador)
                        .append("noticiaId", noticiaId)
                        .append("comentario", comentarioTexto)
                        .append("fechaHora", Date())
                    println(escribirComentario(usuariosCollection, comentariosCollection, usuarioIdentificador, comentario))
                }
                5 -> {
                    // Listar comentarios de una noticia
                    println("\n-- Listar Comentarios de una Noticia --")
                    print("Ingrese el título de la noticia: ")
                    val noticiaId = readln()
                    val comentarios = listarComentariosPorNoticia(comentariosCollection, noticiaId)
                    if (comentarios.isEmpty()) {
                        println("No hay comentarios para la noticia \"$noticiaId\"")
                    } else {
                        comentarios.forEach { println(it.toJson()) }
                    }
                }
                6 -> {
                    // Buscar noticias por etiqueta
                    println("\n-- Buscar Noticias por Etiqueta --")
                    print("Ingrese la etiqueta: ")
                    val etiqueta = readln()
                    val noticias = buscarNoticiasPorEtiqueta(noticiasCollection, etiqueta)
                    if (noticias.isEmpty()) {
                        println("No se encontraron noticias con la etiqueta \"$etiqueta\"")
                    } else {
                        noticias.forEach { println(it.toJson()) }
                    }
                }
                7 -> {
                    // Listar las 10 últimas noticias
                    println("\n-- Últimas 10 Noticias Publicadas --")
                    val ultimasNoticias = listarUltimasNoticias(noticiasCollection)
                    if (ultimasNoticias.isEmpty()) {
                        println("No hay noticias registradas.")
                    } else {
                        ultimasNoticias.forEach { println(it.toJson()) }
                    }
                }
                8 -> {
                    println("Saliendo...")
                    break
                }
                else -> println("Opción no válida. Intenta de nuevo.")
            }
        }
    } catch (e: Exception) {
        println("Error: ${e.message}")
    } finally {
        ConexionMongo.close()
    }
}

fun registrarUsuario(usuariosCollection: MongoCollection<Document>, usuario: Document): String {
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

fun publicarNoticia(noticiasCollection: MongoCollection<Document>, noticia: Document): String {
    val filtroFecha = Filters.eq("fechaPublicacion", noticia.getDate("fechaPublicacion"))
    if (noticiasCollection.find(filtroFecha).first() != null) {
        return "Error: Ya existe una noticia con la misma fecha de publicación."
    }
    noticiasCollection.insertOne(noticia)
    return "Noticia publicada correctamente."
}

fun listarNoticiasPorUsuario(noticiasCollection: MongoCollection<Document>, autor: String): List<Document> {
    return noticiasCollection.find(Filters.eq("autor", autor)).toList()
}

fun escribirComentario(
    usuariosCollection: MongoCollection<Document>,
    comentariosCollection: MongoCollection<Document>,
    usuarioIdentificador: String,
    comentario: Document
): String {
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

fun listarComentariosPorNoticia(comentariosCollection: MongoCollection<Document>, noticiaId: String): List<Document> {
    return comentariosCollection.find(Filters.eq("noticiaId", noticiaId)).toList()
}

fun buscarNoticiasPorEtiqueta(noticiasCollection: MongoCollection<Document>, etiqueta: String): List<Document> {
    return noticiasCollection.find(Filters.eq("tags", etiqueta)).toList()
}

fun listarUltimasNoticias(noticiasCollection: MongoCollection<Document>): List<Document> {
    return noticiasCollection.find()
        .sort(Sorts.descending("fechaPublicacion"))
        .limit(10)
        .toList()
}