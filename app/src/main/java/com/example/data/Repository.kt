package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers

class LogisticsRepository(private val dao: LogisticsDao) {

    val allMagasins: Flow<List<Magasin>> = dao.getAllMagasins()
    val allChargements: Flow<List<ChargementArchive>> = dao.getAllChargements()
    val allDechargements: Flow<List<DechargementArchive>> = dao.getAllDechargements()
    val allControlesRemorques: Flow<List<ControleRemorqueArchive>> = dao.getAllControlesRemorques()
    val allControlesExports: Flow<List<ControleExportArchive>> = dao.getAllControlesExports()

    suspend fun insertMagasin(nom: String): Long = withContext(Dispatchers.IO) {
        dao.insertMagasin(Magasin(nom = nom))
    }

    suspend fun deleteMagasin(id: Int) = withContext(Dispatchers.IO) {
        dao.deleteMagasin(id)
    }

    suspend fun insertChargement(chargement: ChargementArchive): Long = withContext(Dispatchers.IO) {
        dao.insertChargement(chargement)
    }

    suspend fun deleteChargement(id: Int) = withContext(Dispatchers.IO) {
        dao.deleteChargement(id)
    }

    suspend fun insertDechargement(dechargement: DechargementArchive) = withContext(Dispatchers.IO) {
        dao.insertDechargement(dechargement)
    }

    suspend fun deleteDechargement(id: Int) = withContext(Dispatchers.IO) {
        dao.deleteDechargement(id)
    }

    suspend fun insertControleRemorque(controle: ControleRemorqueArchive) = withContext(Dispatchers.IO) {
        dao.insertControleRemorque(controle)
    }

    suspend fun deleteControleRemorque(id: Int) = withContext(Dispatchers.IO) {
        dao.deleteControleRemorque(id)
    }

    suspend fun insertControleExport(controle: ControleExportArchive) = withContext(Dispatchers.IO) {
        dao.insertControleExport(controle)
    }

    suspend fun deleteControleExport(id: Int) = withContext(Dispatchers.IO) {
        dao.deleteControleExport(id)
    }

    // Initialize preset warehouses if the table is currently empty
    suspend fun populatePresetsIfNeeded() = withContext(Dispatchers.IO) {
        val current = dao.getAllMagasins().first()
        if (current.isEmpty()) {
            val presets = listOf(
                "Magasin Principal",
                "Magasin Transit",
                "Magasin Export",
                "Dépôt Nord"
            )
            presets.forEach {
                dao.insertMagasin(Magasin(nom = it))
            }
        }
    }
}
