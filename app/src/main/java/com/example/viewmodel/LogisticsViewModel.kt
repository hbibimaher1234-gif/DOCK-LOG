package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class Screen {
    DASHBOARD,
    CHARGEMENT,
    DECHARGEMENT,
    CONTROLE_REMORQUE,
    CONTROLE_EXPORT,
    HISTORIQUE
}

class LogisticsViewModel(private val repository: LogisticsRepository) : ViewModel() {

    // Populated database lists
    val magasins: StateFlow<List<Magasin>> = repository.allMagasins
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val chargements: StateFlow<List<ChargementArchive>> = repository.allChargements
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dechargements: StateFlow<List<DechargementArchive>> = repository.allDechargements
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val controlesRemorques: StateFlow<List<ControleRemorqueArchive>> = repository.allControlesRemorques
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val controlesExports: StateFlow<List<ControleExportArchive>> = repository.allControlesExports
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.populatePresetsIfNeeded()
        }
    }

    // Navigation state
    val currentScreen = MutableStateFlow(Screen.DASHBOARD)

    // Global alert system
    val alertToastMessage = MutableStateFlow<String?>(null)

    // ==========================================
    // 1. CHARGEMENT STOCK ACTIVE STATE
    // ==========================================
    val chargementCodeLivraison = MutableStateFlow("")
    val chargementNombreColis = MutableStateFlow("")
    val chargementSelectedMagasin = MutableStateFlow("")
    val chargementMatriculeAgent = MutableStateFlow("")

    val chargementColisNum = MutableStateFlow("")
    val chargementColisArticle = MutableStateFlow("")
    val chargementColisQuantite = MutableStateFlow("1")

    val activeChargementItems = MutableStateFlow<List<ChargementItem>>(emptyList())
    val chargementErrorPrompt = MutableStateFlow<String?>(null)
    val chargementSuccessPrompt = MutableStateFlow<String?>(null)

    fun addChargementItem() {
        val num = chargementColisNum.value.trim()
        val art = chargementColisArticle.value.trim()
        val qtyStr = chargementColisQuantite.value.trim()
        val qty = qtyStr.toIntOrNull() ?: 0

        if (num.isEmpty() || art.isEmpty() || qty <= 0) {
            chargementErrorPrompt.value = "Veuillez remplir correctement les champs du colis."
            return
        }

        // 1. Anti-doublon check
        val alreadyExists = activeChargementItems.value.any { it.numeroColis.equals(num, ignoreCase = true) }
        if (alreadyExists) {
            chargementErrorPrompt.value = "Erreur : Ce colis est déjà présent dans la liste."
            return
        }

        chargementErrorPrompt.value = null

        // Add to active list
        val item = ChargementItem(numeroColis = num, article = art, quantite = qty)
        activeChargementItems.value = activeChargementItems.value + item

        // Clear subfields
        chargementColisNum.value = ""
        chargementColisArticle.value = ""
        chargementColisQuantite.value = "1"

        // Check completion count (Table columns = target loading target)
        checkChargementCompleted()
    }

    fun removeChargementItem(item: ChargementItem) {
        activeChargementItems.value = activeChargementItems.value - item
        checkChargementCompleted()
    }

    private fun checkChargementCompleted() {
        val target = chargementNombreColis.value.toIntOrNull() ?: -1
        if (target > 0 && activeChargementItems.value.size == target) {
            chargementSuccessPrompt.value = "Chargement terminé ! Veuillez valider pour passer à l'étape suivante."
        } else {
            chargementSuccessPrompt.value = null
        }
    }

    fun archiveActiveChargement() {
        val code = chargementCodeLivraison.value.trim()
        val targetStr = chargementNombreColis.value.trim()
        val target = targetStr.toIntOrNull() ?: 0
        val magasin = chargementSelectedMagasin.value
        val agent = chargementMatriculeAgent.value.trim()
        val items = activeChargementItems.value

        if (code.isEmpty() || items.isEmpty() || magasin.isEmpty() || agent.isEmpty()) {
            chargementErrorPrompt.value = "Veuillez remplir l'en-tête et ajouter des colis."
            return
        }

        viewModelScope.launch {
            val archive = ChargementArchive(
                codeLivraison = code,
                nombreColis = target,
                magasin = magasin,
                matriculeAgent = agent,
                itemsJson = ModelSerializer.serializeChargementItems(items)
            )
            repository.insertChargement(archive)
            resetChargementFields()
            alertToastMessage.value = "Chargement archivé avec succès !"
            currentScreen.value = Screen.DASHBOARD
        }
    }

    fun resetChargementFields() {
        chargementCodeLivraison.value = ""
        chargementNombreColis.value = ""
        chargementSelectedMagasin.value = magasins.value.firstOrNull()?.nom ?: ""
        chargementMatriculeAgent.value = ""
        chargementColisNum.value = ""
        chargementColisArticle.value = ""
        chargementColisQuantite.value = "1"
        activeChargementItems.value = emptyList()
        chargementErrorPrompt.value = null
        chargementSuccessPrompt.value = null
    }

    // ==========================================
    // 2. DECHARGEMENT STOCK ACTIVE STATE
    // ==========================================
    val dechargementNbl = MutableStateFlow("")
    val dechargementNombreColis = MutableStateFlow("")
    val dechargementSelectedMagasin = MutableStateFlow("")
    val dechargementMatriculeAgent = MutableStateFlow("")

    val dechargementColisArticle = MutableStateFlow("")
    val dechargementColisQuantite = MutableStateFlow("1")

    val activeDechargementItems = MutableStateFlow<List<DechargementItem>>(emptyList())
    val dechargementErrorPrompt = MutableStateFlow<String?>(null)
    val dechargementSuccessPrompt = MutableStateFlow<String?>(null)

    fun addDechargementItem() {
        val art = dechargementColisArticle.value.trim()
        val qtyStr = dechargementColisQuantite.value.trim()
        val qty = qtyStr.toIntOrNull() ?: 0

        if (art.isEmpty() || qty <= 0) {
            dechargementErrorPrompt.value = "Veuillez remplir correctement les champs de déchargement."
            return
        }

        dechargementErrorPrompt.value = null

        // Add to active list
        val item = DechargementItem(article = art, quantite = qty)
        activeDechargementItems.value = activeDechargementItems.value + item

        // Clear subfields
        dechargementColisArticle.value = ""
        dechargementColisQuantite.value = "1"

        checkDechargementCompleted()
    }

    fun removeDechargementItem(item: DechargementItem) {
        activeDechargementItems.value = activeDechargementItems.value - item
        checkDechargementCompleted()
    }

    private fun checkDechargementCompleted() {
        val target = dechargementNombreColis.value.toIntOrNull() ?: -1
        // Allow check against either total count of loaded units or size of rows
        val totalUnloadedQty = activeDechargementItems.value.sumOf { it.quantite }
        val sizeLines = activeDechargementItems.value.size
        
        if (target > 0 && (totalUnloadedQty == target || sizeLines == target)) {
            dechargementSuccessPrompt.value = "Déchargement terminé ! Veuillez valider pour passer à l'étape suivante."
        } else {
            dechargementSuccessPrompt.value = null
        }
    }

    fun archiveActiveDechargement() {
        val nblVal = dechargementNbl.value.trim()
        val targetStr = dechargementNombreColis.value.trim()
        val target = targetStr.toIntOrNull() ?: 0
        val magasin = dechargementSelectedMagasin.value
        val agent = dechargementMatriculeAgent.value.trim()
        val items = activeDechargementItems.value

        if (nblVal.isEmpty() || items.isEmpty() || magasin.isEmpty() || agent.isEmpty()) {
            dechargementErrorPrompt.value = "Veuillez remplir l'en-tête et ajouter des articles."
            return
        }

        viewModelScope.launch {
            val archive = DechargementArchive(
                nbl = nblVal,
                nombreColis = target,
                magasin = magasin,
                matriculeAgent = agent,
                itemsJson = ModelSerializer.serializeDechargementItems(items)
            )
            repository.insertDechargement(archive)
            resetDechargementFields()
            alertToastMessage.value = "Déchargement archivé avec succès !"
            currentScreen.value = Screen.DASHBOARD
        }
    }

    fun resetDechargementFields() {
        dechargementNbl.value = ""
        dechargementNombreColis.value = ""
        dechargementSelectedMagasin.value = magasins.value.firstOrNull()?.nom ?: ""
        dechargementMatriculeAgent.value = ""
        dechargementColisArticle.value = ""
        dechargementColisQuantite.value = "1"
        activeDechargementItems.value = emptyList()
        dechargementErrorPrompt.value = null
        dechargementSuccessPrompt.value = null
    }


    // ==========================================
    // 3. CONTROLE REMORQUE VIDE ACTIVE STATE
    // ==========================================
    val remorqueInspecteur = MutableStateFlow("")
    val remorqueMatricule = MutableStateFlow("")
    val remorqueChecklist = MutableStateFlow<Map<String, String>>(emptyMap())
    val remorqueRemarques = MutableStateFlow("")
    val remorqueDecision = MutableStateFlow("Accepté")

    val remorqueError = MutableStateFlow<String?>(null)

    val remorquePoints = listOf(
        "Conformité du toit (absence de trous...)",
        "Conformité des cotés (absence de trou)",
        "Conformité de la planche bien entretenue",
        "Niveau de suspension",
        "Les anneaux du plomb",
        "Présence de deux sangles - Barre",
        "Cale de roue installée"
    )

    fun setRemorqueCheckPoint(point: String, status: String) {
        val updated = remorqueChecklist.value.toMutableMap()
        updated[point] = status
        remorqueChecklist.value = updated
    }

    fun archiveControleRemorque() {
        val insp = remorqueInspecteur.value.trim()
        val mat = remorqueMatricule.value.trim()
        val rem = remorqueRemarques.value.trim()
        val dec = remorqueDecision.value

        if (insp.isEmpty() || mat.isEmpty()) {
            remorqueError.value = "Veuillez renseigner le nom de l'inspecteur et le numéro de remorque."
            return
        }

        viewModelScope.launch {
            val archive = ControleRemorqueArchive(
                inspecteur = insp,
                matriculeRemorque = mat,
                pointsJson = ModelSerializer.serializeChecklist(remorqueChecklist.value),
                remarques = rem,
                decision = dec
            )
            repository.insertControleRemorque(archive)
            resetControleRemorqueFields()
            alertToastMessage.value = "Contrôle Remorque archivé !"
            currentScreen.value = Screen.DASHBOARD
        }
    }

    fun resetControleRemorqueFields() {
        remorqueInspecteur.value = ""
        remorqueMatricule.value = ""
        remorqueChecklist.value = emptyMap()
        remorqueRemarques.value = ""
        remorqueDecision.value = "Accepté"
        remorqueError.value = null
    }


    // ==========================================
    // 4. CONTROLE EXPORT AVANT CHARGEMENT STATE
    // ==========================================
    val exportInspecteur = MutableStateFlow("")
    val exportMatricule = MutableStateFlow("")
    val exportChecklist = MutableStateFlow<Map<String, String>>(emptyMap())
    val exportRemarques = MutableStateFlow("")
    val exportDecision = MutableStateFlow("Accepté")

    val exportError = MutableStateFlow<String?>(null)

    val exportPoints = listOf(
        "Propreté de la remorque (balayée/propre)",
        "Absence d'odeurs suspectes ou fortes",
        "Absence d'humidité/remorque bien sèche",
        "État de fermeture des portes arrières",
        "Présence du plomb d'origine ou scellé",
        "Vérification des documents de transport",
        "Conformité de l'étiquetage d'exportation"
    )

    fun setExportCheckPoint(point: String, status: String) {
        val updated = exportChecklist.value.toMutableMap()
        updated[point] = status
        exportChecklist.value = updated
    }

    fun archiveControleExport() {
        val insp = exportInspecteur.value.trim()
        val mat = exportMatricule.value.trim()
        val rem = exportRemarques.value.trim()
        val dec = exportDecision.value

        if (insp.isEmpty() || mat.isEmpty()) {
            exportError.value = "Veuillez renseigner le nom de l'inspecteur et le numéro de remorque."
            return
        }

        viewModelScope.launch {
            val archive = ControleExportArchive(
                inspecteur = insp,
                matriculeRemorque = mat,
                pointsJson = ModelSerializer.serializeChecklist(exportChecklist.value),
                remarques = rem,
                decision = dec
            )
            repository.insertControleExport(archive)
            resetControleExportFields()
            alertToastMessage.value = "Contrôle Export archivé !"
            currentScreen.value = Screen.DASHBOARD
        }
    }

    fun resetControleExportFields() {
        exportInspecteur.value = ""
        exportMatricule.value = ""
        exportChecklist.value = emptyMap()
        exportRemarques.value = ""
        exportDecision.value = "Accepté"
        exportError.value = null
    }

    // ==========================================
    // MAGASIN DYNAMIC ADDITION
    // ==========================================
    fun addMagasin(nom: String) {
        if (nom.trim().isEmpty()) return
        viewModelScope.launch {
            repository.insertMagasin(nom.trim())
            // Safely update selected warehouses as backup
            if (chargementSelectedMagasin.value.isEmpty()) {
                chargementSelectedMagasin.value = nom.trim()
            }
            if (dechargementSelectedMagasin.value.isEmpty()) {
                dechargementSelectedMagasin.value = nom.trim()
            }
        }
    }

    fun deleteMagasin(id: Int) {
        viewModelScope.launch {
            repository.deleteMagasin(id)
        }
    }

    // ==========================================
    // DELETION FROM HISTORIQUE
    // ==========================================
    fun deleteChargement(id: Int) {
        viewModelScope.launch { repository.deleteChargement(id) }
    }

    fun deleteDechargement(id: Int) {
        viewModelScope.launch { repository.deleteDechargement(id) }
    }

    fun deleteControleRemorque(id: Int) {
        viewModelScope.launch { repository.deleteControleRemorque(id) }
    }

    fun deleteControleExport(id: Int) {
        viewModelScope.launch { repository.deleteControleExport(id) }
    }
}

// Simple provider factory for our ViewModel
class LogisticsViewModelFactory(private val repository: LogisticsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LogisticsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LogisticsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
