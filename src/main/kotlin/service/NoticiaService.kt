package service

import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Sorts
import org.bson.Document
import utils.ConexionMongo

class NoticiaService(database: MongoDatabase) {
    private val databaseName = "noticiasdb"
    // Se obtiene la colección "noticias" de la base de datos
    private val noticiasCollection: MongoCollection<Document> = ConexionMongo.getCollection(databaseName, "collNoticias")

    /**
     * Publica una noticia comprobando que la fecha de publicación sea única.
     * La fecha de publicación es única y no se puede modificar posteriormente.
     */
    fun publicarNoticia(noticia: Document): String {
        val filtroFecha = Filters.eq("fechaPublicacion", noticia.getDate("fechaPublicacion"))
        if (noticiasCollection.find(filtroFecha).first() != null) {
            return "Error: Ya existe una noticia con la misma fecha de publicación."
        }
        noticiasCollection.insertOne(noticia)
        return "Noticia publicada correctamente."
    }

    /**
     * Lista las noticias publicadas por un usuario determinado.
     */
    fun listarNoticiasPorUsuario(autor: String): List<Document> {
        return noticiasCollection.find(Filters.eq("autor", autor)).toList()
    }

    /**
     * Busca noticias que contengan una etiqueta específica.
     */
    fun buscarNoticiasPorEtiqueta(etiqueta: String): List<Document> {
        return noticiasCollection.find(Filters.eq("tags", etiqueta)).toList()
    }

    /**
     * Lista las 10 últimas noticias publicadas ordenadas de forma descendente por fecha.
     */
    fun listarUltimasNoticias(): List<Document> {
        return noticiasCollection.find()
            .sort(Sorts.descending("fechaPublicacion"))
            .limit(10)
            .toList()
    }
}