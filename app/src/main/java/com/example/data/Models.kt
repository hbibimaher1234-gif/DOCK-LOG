package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = "magasins")
data class Magasin(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nom: String
)

@Entity(tableName = "chargements")
data class ChargementArchive(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val codeLivraison: String,
    val nombreColis: Int,
    val magasin: String,
    val matriculeAgent: String,
    val date: Long = System.currentTimeMillis(),
    val itemsJson: String // Structured as a serialized helper list
)

@Entity(tableName = "dechargements")
data class DechargementArchive(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nbl: String,
    val nombreColis: Int,
    val magasin: String,
    val matriculeAgent: String,
    val date: Long = System.currentTimeMillis(),
    val itemsJson: String // Structured list of articles
)

@Entity(tableName = "contrôles_remorques")
data class ControleRemorqueArchive(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val inspecteur: String,
    val matriculeRemorque: String,
    val date: Long = System.currentTimeMillis(),
    val pointsJson: String, // Map serialized to string: Key=Point, Value=Status (OK/N_OK/NA)
    val remarques: String,
    val decision: String // Accepté, Refusé, Accepté sous réserve
)

@Entity(tableName = "contrôles_exports")
data class ControleExportArchive(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val inspecteur: String,
    val matriculeRemorque: String,
    val date: Long = System.currentTimeMillis(),
    val pointsJson: String, // Map serialized to string
    val remarques: String,
    val decision: String // Accepté, Refusé, Accepté sous réserve
)

// Simple helpers to avoid heavy JSON libraries and potential compile-time failures
data class ChargementItem(
    val numeroColis: String,
    val article: String,
    val quantite: Int
) {
    fun serialize(): String = "$numeroColis|:|:$article|:|:$quantite"
    
    companion object {
        fun deserialize(str: String): ChargementItem? {
            val parts = str.split("|:|:")
            if (parts.size >= 3) {
                return ChargementItem(
                    numeroColis = parts[0],
                    article = parts[1],
                    quantite = parts[2].toIntOrNull() ?: 1
                )
            }
            return null
        }
    }
}

data class DechargementItem(
    val article: String,
    val quantite: Int
) {
    fun serialize(): String = "$article|:|:$quantite"
    
    companion object {
        fun deserialize(str: String): DechargementItem? {
            val parts = str.split("|:|:")
            if (parts.size >= 2) {
                return DechargementItem(
                    article = parts[0],
                    quantite = parts[1].toIntOrNull() ?: 1
                )
            }
            return null
        }
    }
}

object ModelSerializer {
    // List support using simple delimiter '|;;|'
    fun serializeChargementItems(items: List<ChargementItem>): String {
        return items.joinToString("|;;|") { it.serialize() }
    }

    fun deserializeChargementItems(str: String): List<ChargementItem> {
        if (str.isEmpty()) return emptyList()
        return str.split("|;;|").mapNotNull { ChargementItem.deserialize(it) }
    }

    fun serializeDechargementItems(items: List<DechargementItem>): String {
        return items.joinToString("|;;|") { it.serialize() }
    }

    fun deserializeDechargementItems(str: String): List<DechargementItem> {
        if (str.isEmpty()) return emptyList()
        return str.split("|;;|").mapNotNull { DechargementItem.deserialize(it) }
    }

    // Map support for checklist: 'key1:value1,key2:value2'
    fun serializeChecklist(map: Map<String, String>): String {
        return map.entries.joinToString("|;;|") { "${it.key}|:|:${it.value}" }
    }

    fun deserializeChecklist(str: String): Map<String, String> {
        if (str.isEmpty()) return emptyMap()
        val result = mutableMapOf<String, String>()
        str.split("|;;|").forEach {
            val parts = it.split("|:|:")
            if (parts.size >= 2) {
                result[parts[0]] = parts[1]
            }
        }
        return result
    }
    
    fun formatDate(timestamp: Long): String {
        return SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(timestamp))
    }
}
