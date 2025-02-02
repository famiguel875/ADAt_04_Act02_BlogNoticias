package utils

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import io.github.cdimascio.dotenv.dotenv
import org.bson.Document

object ConexionMongo {
    private val mongoClient: MongoClient by lazy {
        val dotenv = dotenv()
        val connectString = dotenv["URL_MONGODB"]
        MongoClients.create(connectString)
    }

    // Obtiene la base de datos solicitada.
    fun getDatabase(bd: String): MongoDatabase {
        return mongoClient.getDatabase(bd)
    }

    // Obtiene la colección solicitada a partir del nombre de la base de datos y el nombre de la colección.
    fun getCollection(databaseName: String, collectionName: String): MongoCollection<Document> {
        return getDatabase(databaseName).getCollection(collectionName)
    }

    fun close() {
        mongoClient.close()
    }
}