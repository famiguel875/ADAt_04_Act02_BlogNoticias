package service

import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Sorts
import model.Noticia

class NoticiaService(database: MongoDatabase) {
    private val collection: MongoCollection<Noticia> =
        database.getCollection("noticias", Noticia::class.java)

    // Publica una noticia comprobando que la fecha de publicación no se repita
    fun publicarNoticia(noticia: Noticia): String {
        val filtroFecha = Filters.eq("fechaPublicacion", noticia.fechaPublicacion)
        if (collection.find(filtroFecha).first() != null) {
            return "No es posible publicar la noticia: la fecha de publicación ya existe."
        }
        collection.insertOne(noticia)
        return "Noticia publicada correctamente."
    }

    // Lista las noticias publicadas por un usuario (se usa el campo 'autor')
    fun listarNoticiasPorUsuario(nombreUsuario: String): List<Noticia> {
        return collection.find(Filters.eq("autor", nombreUsuario)).toList()
    }

    // Busca noticias que contengan una etiqueta determinada
    fun listarNoticiasPorEtiqueta(tag: String): List<Noticia> {
        // Se asume que 'tags' es un array, por lo que se puede usar Filters.eq para buscar coincidencias exactas
        return collection.find(Filters.eq("tags", tag)).toList()
    }

    // Lista las 10 últimas noticias publicadas ordenadas por fecha descendente
    fun listarUltimasNoticias(cantidad: Int = 10): List<Noticia> {
        return collection.find()
            .sort(Sorts.descending("fechaPublicacion"))
            .limit(cantidad)
            .toList()
    }
}