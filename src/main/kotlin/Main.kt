import org.bson.Document
import java.util.Date
import com.mongodb.client.MongoDatabase
import service.UsuarioService
import service.ComentarioService
import service.NoticiaService
import utils.ConexionMongo

fun main() {
    // Usamos "noticiasdb" como nombre de la base de datos.
    val databaseName = "noticiasdb"
    val database: MongoDatabase = ConexionMongo.getDatabase(databaseName)

    // Instanciamos los servicios, inyectando la base de datos.
    val usuarioService = UsuarioService(database)
    val noticiaService = NoticiaService(database)
    val comentarioService = ComentarioService(database)

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

                    println(usuarioService.registrarUsuario(usuario))
                }
                2 -> {
                    // Publicar noticia
                    println("\n-- Publicar Noticia --")
                    print("Título: ")
                    val titulo = readln()
                    print("Cuerpo: ")
                    val cuerpo = readln()
                    print("Autor (nombre de usuario o email): ")
                    val autor = readln()
                    print("Etiquetas (separadas por coma, opcional): ")
                    val tagsInput = readln()
                    val tags = if (tagsInput.isBlank()) listOf<String>() else tagsInput.split(",").map { it.trim() }

                    // Se utiliza la fecha actual para la publicación.
                    val fechaPublicacion = Date()

                    val noticia = Document("titulo", titulo)
                        .append("cuerpo", cuerpo)
                        .append("fechaPublicacion", fechaPublicacion)
                        .append("autor", autor)
                        .append("tags", tags)

                    // Verificar que el usuario esté registrado y activo
                    val filtroAutor = com.mongodb.client.model.Filters.or(
                        com.mongodb.client.model.Filters.eq("email", autor),
                        com.mongodb.client.model.Filters.eq("nombreUsuario", autor)
                    )
                    val usuarioDoc = usuarioService.getUsuario(filtroAutor)
                    if (usuarioDoc == null) {
                        println("Error: El usuario no está registrado y no puede publicar noticias.")
                    } else if (usuarioDoc.getString("estado") != "ACTIVO") {
                        println("Error: El usuario no está en estado ACTIVO y no puede publicar noticias.")
                    } else {
                        println(noticiaService.publicarNoticia(noticia))
                    }
                }
                3 -> {
                    // Listar noticias por usuario
                    println("\n-- Listar Noticias por Usuario --")
                    print("Ingrese el nombre de usuario: ")
                    val autor = readln()
                    val noticias = noticiaService.listarNoticiasPorUsuario(autor)
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

                    println(comentarioService.escribirComentario(usuarioIdentificador, comentario))
                }
                5 -> {
                    // Listar comentarios de una noticia
                    println("\n-- Listar Comentarios de una Noticia --")
                    print("Ingrese el título de la noticia: ")
                    val noticiaId = readln()
                    val comentarios = comentarioService.listarComentariosPorNoticia(noticiaId)
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
                    val noticias = noticiaService.buscarNoticiasPorEtiqueta(etiqueta)
                    if (noticias.isEmpty()) {
                        println("No se encontraron noticias con la etiqueta \"$etiqueta\"")
                    } else {
                        noticias.forEach { println(it.toJson()) }
                    }
                }
                7 -> {
                    // Listar las 10 últimas noticias
                    println("\n-- Últimas 10 Noticias Publicadas --")
                    val ultimasNoticias = noticiaService.listarUltimasNoticias()
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
