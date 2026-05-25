package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LogisticsDao {
    // Magasins
    @Query("SELECT * FROM magasins ORDER BY nom ASC")
    fun getAllMagasins(): Flow<List<Magasin>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMagasin(magasin: Magasin): Long

    @Query("DELETE FROM magasins WHERE id = :id")
    suspend fun deleteMagasin(id: Int)

    // Chargement
    @Query("SELECT * FROM chargements ORDER BY date DESC")
    fun getAllChargements(): Flow<List<ChargementArchive>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChargement(chargement: ChargementArchive): Long

    @Query("DELETE FROM chargements WHERE id = :id")
    suspend fun deleteChargement(id: Int)

    // Déchargement
    @Query("SELECT * FROM dechargements ORDER BY date DESC")
    fun getAllDechargements(): Flow<List<DechargementArchive>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDechargement(dechargement: DechargementArchive): Long

    @Query("DELETE FROM dechargements WHERE id = :id")
    suspend fun deleteDechargement(id: Int)

    // Contrôle Remorque (Inspection)
    @Query("SELECT * FROM controles_remorques ORDER BY date DESC")
    fun getAllControlesRemorques(): Flow<List<ControleRemorqueArchive>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertControleRemorque(controle: ControleRemorqueArchive): Long

    @Query("DELETE FROM controles_remorques WHERE id = :id")
    suspend fun deleteControleRemorque(id: Int)

    // Contrôle Export Avant Chargement
    @Query("SELECT * FROM controles_exports ORDER BY date DESC")
    fun getAllControlesExports(): Flow<List<ControleExportArchive>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertControleExport(controle: ControleExportArchive): Long

    @Query("DELETE FROM controles_exports WHERE id = :id")
    suspend fun deleteControleExport(id: Int)
}
