package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.*
import com.example.utils.Sharer
import com.example.viewmodel.LogisticsViewModel
import com.example.viewmodel.Screen

@Composable
fun MainLogisticsApp(viewModel: LogisticsViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val toastMsg by viewModel.alertToastMessage.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Display Toast when necessary
    LaunchedEffect(toastMsg) {
        toastMsg?.let {
            android.widget.Toast.makeText(context, it, android.widget.Toast.LENGTH_SHORT).show()
            viewModel.alertToastMessage.value = null
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Crossfade(targetState = currentScreen, label = "ScreenTransition") { screen ->
            when (screen) {
                Screen.DASHBOARD -> DashboardScreen(viewModel)
                Screen.CHARGEMENT -> ChargementScreen(viewModel)
                Screen.DECHARGEMENT -> DechargementScreen(viewModel)
                Screen.CONTROLE_REMORQUE -> ControleRemorqueScreen(viewModel)
                Screen.CONTROLE_EXPORT -> ControleExportScreen(viewModel)
                Screen.HISTORIQUE -> HistoriqueScreen(viewModel)
            }
        }

        val barcodeScannerActive by viewModel.barcodeScannerActive.collectAsStateWithLifecycle()
        if (barcodeScannerActive) {
            BarcodeScannerDialog(viewModel = viewModel)
        }
    }
}

// ==========================================
// 1. DASHBOARD SCREEN
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: LogisticsViewModel) {
    val context = LocalContext.current
    val totalLoadings by viewModel.chargements.collectAsStateWithLifecycle()
    val totalUnloadings by viewModel.dechargements.collectAsStateWithLifecycle()
    val totalInspections by viewModel.controlesRemorques.collectAsStateWithLifecycle()
    val totalExports by viewModel.controlesExports.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = Color(0xFFF7F9FC),
        topBar = {
            Column(
                modifier = Modifier
                    .background(Color.White)
                    .statusBarsPadding()
                    .padding(top = 18.dp, start = 20.dp, end = 20.dp, bottom = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            text = "LOGISTIQUE PRO",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.5.sp
                            ),
                            color = Color(0xFF2563EB)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Tableau de bord",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp
                            ),
                            color = Color(0xFF1B1B1F)
                        )
                    }

                    // Avatar A7 representation
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .background(Color(0xFFDBEAFE))
                            .border(2.dp, Color.White, RoundedCornerShape(22.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "A7",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1D4ED8)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Pulsing Agent Badge
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFEFF6FF), RoundedCornerShape(16.dp))
                        .border(1.dp, Color(0xFFDBEAFE), RoundedCornerShape(16.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PulsingStatusDot()
                    Text(
                        text = "Agent 724 — Connecté (Dépôt Nord)",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = Color(0xFF475569)
                    )
                }
            }
        },
        bottomBar = {
            Surface(
                modifier = Modifier.navigationBarsPadding(),
                color = Color.White,
                tonalElevation = 8.dp,
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { viewModel.currentScreen.value = Screen.DASHBOARD }
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Accueil",
                            tint = Color(0xFF2563EB),
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "ACCUEIL",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            ),
                            color = Color(0xFF2563EB)
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                viewModel.alertToastMessage.value = "Prêt pour le scan de codes-barres"
                            }
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = "Scan",
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "SCAN",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            ),
                            color = Color(0xFF94A3B8)
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                viewModel.alertToastMessage.value = "Profil utilisateur: Agent 724"
                            }
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profil",
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "PROFIL",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            ),
                            color = Color(0xFF94A3B8)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            
            // Primary Operations Title
            Text(
                text = "Opérations Logistiques",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF1B1B1F)
                ),
                modifier = Modifier.padding(bottom = 2.dp)
            )

            // High Density Grid layout matching tailwind columns precisely
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GridButtonCard(
                    title = "Chargement\nStock",
                    subtitle = "Nouveaux colis",
                    icon = Icons.Default.MoveToInbox,
                    iconBgColor = Color(0xFF2563EB),
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.currentScreen.value = Screen.CHARGEMENT }
                )
                GridButtonCard(
                    title = "Déchargement\nStock",
                    subtitle = "Réception NBL",
                    icon = Icons.Default.Unarchive,
                    iconBgColor = Color(0xFF4F46E5),
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.currentScreen.value = Screen.DECHARGEMENT }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GridButtonCard(
                    title = "Remorque\nExport",
                    subtitle = "Checklist Vide",
                    icon = Icons.Default.LocalShipping,
                    iconBgColor = Color(0xFF1E293B),
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.currentScreen.value = Screen.CONTROLE_REMORQUE }
                )
                GridButtonCard(
                    title = "Export\nContrôle",
                    subtitle = "Avant chargement",
                    icon = Icons.Default.CheckCircle,
                    iconBgColor = Color(0xFFEA580C),
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.currentScreen.value = Screen.CONTROLE_EXPORT }
                )
            }

            // Dark Charcoal Capsule: Historique & Archives
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.currentScreen.value = Screen.HISTORIQUE },
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                shape = RoundedCornerShape(28.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color.White.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Historique & Archives",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                ),
                                color = Color.White
                            )
                            val totalOps = totalLoadings.size + totalUnloadings.size + totalInspections.size + totalExports.size
                            Text(
                                text = "$totalOps opérations enregistrées",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Ouvrir",
                        tint = Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Real-time Telemetry Stats Footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatFooterCard(
                    label = "Stock",
                    count = "${totalLoadings.size + totalUnloadings.size}",
                    countColor = Color(0xFF2563EB),
                    modifier = Modifier.weight(1f)
                )
                StatFooterCard(
                    label = "Export",
                    count = "${totalInspections.size + totalExports.size}",
                    countColor = Color(0xFFEA580C),
                    modifier = Modifier.weight(1f)
                )
                StatFooterCard(
                    label = "Erreurs",
                    count = "0",
                    countColor = Color(0xFFEF4444),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun GridButtonCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBgColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(132.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        lineHeight = 15.sp
                    ),
                    color = Color(0xFF1B1B1F)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 8.5.sp,
                        color = Color(0xFF94A3B8)
                    )
                )
            }
        }
    }
}

@Composable
fun StatFooterCard(
    label: String,
    count: String,
    countColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp,
                    color = Color(0xFF94A3B8)
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = count,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp
                ),
                color = countColor
            )
        }
    }
}



// ==========================================
// 2. CHARGEMENT STOCK SCREEN
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChargementScreen(viewModel: LogisticsViewModel) {
    val context = LocalContext.current
    val codeLivraison by viewModel.chargementCodeLivraison.collectAsStateWithLifecycle()
    val targetColis by viewModel.chargementNombreColis.collectAsStateWithLifecycle()
    val selectedMagasin by viewModel.chargementSelectedMagasin.collectAsStateWithLifecycle()
    val matriculeAgent by viewModel.chargementMatriculeAgent.collectAsStateWithLifecycle()
    val headerArticle by viewModel.chargementHeaderArticle.collectAsStateWithLifecycle()
    val headerQuantite by viewModel.chargementHeaderQuantite.collectAsStateWithLifecycle()
    
    // Subform colis items
    val colNum by viewModel.chargementColisNum.collectAsStateWithLifecycle()
    val colArt by viewModel.chargementColisArticle.collectAsStateWithLifecycle()
    val colQty by viewModel.chargementColisQuantite.collectAsStateWithLifecycle()

    val activeItems by viewModel.activeChargementItems.collectAsStateWithLifecycle()
    val errorPrompt by viewModel.chargementErrorPrompt.collectAsStateWithLifecycle()
    val successPrompt by viewModel.chargementSuccessPrompt.collectAsStateWithLifecycle()
    
    val magasinsList by viewModel.magasins.collectAsStateWithLifecycle()

    var showAddWarehouseDialog by remember { mutableStateOf(false) }

    // Synchronize default warehouse choice if blank
    LaunchedEffect(magasinsList) {
        if (selectedMagasin.isEmpty() && magasinsList.isNotEmpty()) {
            viewModel.chargementSelectedMagasin.value = magasinsList.first().nom
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chargement Stock") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.currentScreen.value = Screen.DASHBOARD }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            
            // SECTION A: DONNÉES PRINCIPALES
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Données Principales (Entête)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    
                    OutlinedTextField(
                        value = codeLivraison,
                        onValueChange = { viewModel.chargementCodeLivraison.value = it },
                        label = { Text("Code Livraison") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        trailingIcon = {
                            IconButton(onClick = { viewModel.triggerBarcodeScan("chargementCodeLivraison") }) {
                                Icon(
                                    imageVector = Icons.Default.QrCodeScanner,
                                    contentDescription = "Scan",
                                    tint = Color(0xFF2563EB)
                                )
                            }
                        }
                    )

                    OutlinedTextField(
                        value = targetColis,
                        onValueChange = { viewModel.chargementNombreColis.value = it },
                        label = { Text("Nombre de colis à charger") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        trailingIcon = {
                            IconButton(onClick = { viewModel.triggerBarcodeScan("chargementNombreColis") }) {
                                Icon(
                                    imageVector = Icons.Default.QrCodeScanner,
                                    contentDescription = "Scan",
                                    tint = Color(0xFF2563EB)
                                )
                            }
                        }
                    )

                    // Magasin Selection with dynamic [+] dialog trigger
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        var expandedDocDropdown by remember { mutableStateOf(false) }
                        
                        Box(modifier = Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = selectedMagasin,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Magasin Stockage") },
                                trailingIcon = {
                                    IconButton(onClick = { expandedDocDropdown = !expandedDocDropdown }) {
                                        Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Ouvrir")
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                            DropdownMenu(
                                expanded = expandedDocDropdown,
                                onDismissRequest = { expandedDocDropdown = false }
                            ) {
                                magasinsList.forEach { mag ->
                                    DropdownMenuItem(
                                        text = { Text(mag.nom) },
                                        onClick = {
                                            viewModel.chargementSelectedMagasin.value = mag.nom
                                            expandedDocDropdown = false
                                        }
                                    )
                                }
                            }
                        }

                        // [+] popup trigger
                        Button(
                            onClick = { showAddWarehouseDialog = true },
                            modifier = Modifier.height(56.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Ajouter Magasin")
                        }
                    }

                    OutlinedTextField(
                        value = matriculeAgent,
                        onValueChange = { viewModel.chargementMatriculeAgent.value = it },
                        label = { Text("Matricule Agent / Chauffeur") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        trailingIcon = {
                            IconButton(onClick = { viewModel.triggerBarcodeScan("chargementMatriculeAgent") }) {
                                Icon(
                                    imageVector = Icons.Default.QrCodeScanner,
                                    contentDescription = "Scan",
                                    tint = Color(0xFF2563EB)
                                )
                            }
                        }
                    )

                    // NEW EN-TÊTE FIELDS: ARTICLE & QUANTITE
                    OutlinedTextField(
                        value = headerArticle,
                        onValueChange = { viewModel.chargementHeaderArticle.value = it },
                        label = { Text("Article (Entête)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        trailingIcon = {
                            IconButton(onClick = { viewModel.triggerBarcodeScan("chargementHeaderArticle") }) {
                                Icon(
                                    imageVector = Icons.Default.QrCodeScanner,
                                    contentDescription = "Scan",
                                    tint = Color(0xFF2563EB)
                                )
                            }
                        }
                    )

                    OutlinedTextField(
                        value = headerQuantite,
                        onValueChange = { viewModel.chargementHeaderQuantite.value = it },
                        label = { Text("Quantité (Entête)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        trailingIcon = {
                            IconButton(onClick = { viewModel.triggerBarcodeScan("chargementHeaderQuantite") }) {
                                Icon(
                                    imageVector = Icons.Default.QrCodeScanner,
                                    contentDescription = "Scan",
                                    tint = Color(0xFF2563EB)
                                )
                            }
                        }
                    )
                }
            }

            // SECTION B: SAISIE DU COLIS
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Saisie du Colis", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    
                    OutlinedTextField(
                        value = colNum,
                        onValueChange = { viewModel.chargementColisNum.value = it },
                        label = { Text("Numéro colis") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        trailingIcon = {
                            IconButton(onClick = { viewModel.triggerBarcodeScan("chargementColisNum") }) {
                                Icon(
                                    imageVector = Icons.Default.QrCodeScanner,
                                    contentDescription = "Scan",
                                    tint = Color(0xFF2563EB)
                                )
                            }
                        }
                    )

                    OutlinedTextField(
                        value = colArt,
                        onValueChange = { viewModel.chargementColisArticle.value = it },
                        label = { Text("Article") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        trailingIcon = {
                            IconButton(onClick = { viewModel.triggerBarcodeScan("chargementColisArticle") }) {
                                Icon(
                                    imageVector = Icons.Default.QrCodeScanner,
                                    contentDescription = "Scan",
                                    tint = Color(0xFF2563EB)
                                )
                            }
                        }
                    )

                    OutlinedTextField(
                        value = colQty,
                        onValueChange = { viewModel.chargementColisQuantite.value = it },
                        label = { Text("Quantité") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        trailingIcon = {
                            IconButton(onClick = { viewModel.triggerBarcodeScan("chargementColisQuantite") }) {
                                Icon(
                                    imageVector = Icons.Default.QrCodeScanner,
                                    contentDescription = "Scan",
                                    tint = Color(0xFF2563EB)
                                )
                            }
                        }
                    )

                    Button(
                        onClick = { viewModel.addChargementItem() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Ajouter")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Ajouter au tableau")
                    }
                }
            }

            // Real-time Validation Feedbacks
            errorPrompt?.let {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Warning, contentDescription = "Attention", tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = it, color = MaterialTheme.colorScheme.onErrorContainer, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            successPrompt?.let {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFC8E6C9)), // Beautiful soft green
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = "Succès", tint = Color(0xFF2E7D32))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = it, color = Color(0xFF1B5E20), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // SECTION C: TABLEAU DE DÉTAILS LIVRAISON
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Lignes chargées (${activeItems.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        val target = targetColis.toIntOrNull() ?: 0
                        if (target > 0) {
                            Text("Cible: ${activeItems.size} / $target", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (activeItems.isEmpty()) {
                        Text(
                            "Aucun colis ajouté pour le moment.",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    } else {
                        // Detailed column rows
                        activeItems.forEachIndexed { index, item ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(12.dp)
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "N° Colis: ${item.numeroColis}",
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Text(
                                            text = "Art: ${item.article} | Qté: ${item.quantite}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    IconButton(onClick = { viewModel.removeChargementItem(item) }) {
                                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Supprimer", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // SECTION E: ACTIONS DE SORTIE
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        val reportText = Sharer.generateChargementText(
                            codeLivraison, 
                            targetColis.toIntOrNull() ?: 0, 
                            selectedMagasin, 
                            matriculeAgent, 
                            activeItems,
                            headerArticle,
                            headerQuantite.toIntOrNull() ?: 1
                        )
                        Sharer.generateAndSharePdf(context, "chargement_$codeLivraison", "Rapport de Chargement", reportText)
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = "PDF")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("PDF")
                }

                OutlinedButton(
                    onClick = {
                        val reportText = Sharer.generateChargementText(
                            codeLivraison, 
                            targetColis.toIntOrNull() ?: 0, 
                            selectedMagasin, 
                            matriculeAgent, 
                            activeItems,
                            headerArticle,
                            headerQuantite.toIntOrNull() ?: 1
                        )
                        Sharer.sendToWhatsApp(context, reportText)
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("WhatsApp")
                }

                Button(
                    onClick = { viewModel.archiveActiveChargement() },
                    modifier = Modifier.weight(1.2f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)), // Success green
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Archiver")
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    // Window dialog to add custom warehouse dynamically
    if (showAddWarehouseDialog) {
        AddWarehouseDialog(
            onDismiss = { showAddWarehouseDialog = false },
            onSave = { nom ->
                viewModel.addMagasin(nom)
                showAddWarehouseDialog = false
            }
        )
    }
}


// ==========================================
// 3. DECHARGEMENT STOCK SCREEN
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DechargementScreen(viewModel: LogisticsViewModel) {
    val context = LocalContext.current
    val nbl by viewModel.dechargementNbl.collectAsStateWithLifecycle()
    val targetColis by viewModel.dechargementNombreColis.collectAsStateWithLifecycle()
    val selectedMagasin by viewModel.dechargementSelectedMagasin.collectAsStateWithLifecycle()
    val matriculeAgent by viewModel.dechargementMatriculeAgent.collectAsStateWithLifecycle()
    val dechargementHeaderQty by viewModel.dechargementHeaderQuantite.collectAsStateWithLifecycle()

    val colArt by viewModel.dechargementColisArticle.collectAsStateWithLifecycle()
    val colQty by viewModel.dechargementColisQuantite.collectAsStateWithLifecycle()

    val activeItems by viewModel.activeDechargementItems.collectAsStateWithLifecycle()
    val errorPrompt by viewModel.dechargementErrorPrompt.collectAsStateWithLifecycle()
    val successPrompt by viewModel.dechargementSuccessPrompt.collectAsStateWithLifecycle()

    val magasinsList by viewModel.magasins.collectAsStateWithLifecycle()

    var showAddWarehouseDialog by remember { mutableStateOf(false) }

    LaunchedEffect(magasinsList) {
        if (selectedMagasin.isEmpty() && magasinsList.isNotEmpty()) {
            viewModel.dechargementSelectedMagasin.value = magasinsList.first().nom
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Déchargement Stock") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.currentScreen.value = Screen.DASHBOARD }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    titleContentColor = MaterialTheme.colorScheme.onSecondary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSecondary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // DONNÉES PRINCIPALES
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Données Principales", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)

                    OutlinedTextField(
                        value = nbl,
                        onValueChange = { viewModel.dechargementNbl.value = it },
                        label = { Text("Numéro Bon de Livraison (NBL)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        trailingIcon = {
                            IconButton(onClick = { viewModel.triggerBarcodeScan("dechargementNbl") }) {
                                Icon(
                                    imageVector = Icons.Default.QrCodeScanner,
                                    contentDescription = "Scan",
                                    tint = Color(0xFF4F46E5)
                                )
                            }
                        }
                    )

                    OutlinedTextField(
                        value = targetColis,
                        onValueChange = { viewModel.dechargementNombreColis.value = it },
                        label = { Text("Nombre de colis à décharger") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        trailingIcon = {
                            IconButton(onClick = { viewModel.triggerBarcodeScan("dechargementNombreColis") }) {
                                Icon(
                                    imageVector = Icons.Default.QrCodeScanner,
                                    contentDescription = "Scan",
                                    tint = Color(0xFF4F46E5)
                                )
                            }
                        }
                    )

                    // Warehouse select dropdown
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        var expandedDocDropdown by remember { mutableStateOf(false) }

                        Box(modifier = Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = selectedMagasin,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Magasin Stockage") },
                                trailingIcon = {
                                    IconButton(onClick = { expandedDocDropdown = !expandedDocDropdown }) {
                                        Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Ouvrir")
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                            DropdownMenu(
                                expanded = expandedDocDropdown,
                                onDismissRequest = { expandedDocDropdown = false }
                            ) {
                                magasinsList.forEach { mag ->
                                    DropdownMenuItem(
                                        text = { Text(mag.nom) },
                                        onClick = {
                                            viewModel.dechargementSelectedMagasin.value = mag.nom
                                            expandedDocDropdown = false
                                        }
                                    )
                                }
                            }
                        }

                        Button(
                            onClick = { showAddWarehouseDialog = true },
                            modifier = Modifier.height(56.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Ajouter Magasin")
                        }
                    }

                    OutlinedTextField(
                        value = matriculeAgent,
                        onValueChange = { viewModel.dechargementMatriculeAgent.value = it },
                        label = { Text("Matricule Agent / Réceptionnaire") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        trailingIcon = {
                            IconButton(onClick = { viewModel.triggerBarcodeScan("dechargementMatriculeAgent") }) {
                                Icon(
                                    imageVector = Icons.Default.QrCodeScanner,
                                    contentDescription = "Scan",
                                    tint = Color(0xFF4F46E5)
                                )
                            }
                        }
                    )

                    // NEW EN-TÊTE FIELD: QUANTITE
                    OutlinedTextField(
                        value = dechargementHeaderQty,
                        onValueChange = { viewModel.dechargementHeaderQuantite.value = it },
                        label = { Text("Quantité (Entête)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        trailingIcon = {
                            IconButton(onClick = { viewModel.triggerBarcodeScan("dechargementHeaderQuantite") }) {
                                Icon(
                                    imageVector = Icons.Default.QrCodeScanner,
                                    contentDescription = "Scan",
                                    tint = Color(0xFF4F46E5)
                                )
                            }
                        }
                    )
                }
            }

            // SAISIE ARTICLE
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Saisie du Colis / Article", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)

                    OutlinedTextField(
                        value = colArt,
                        onValueChange = { viewModel.dechargementColisArticle.value = it },
                        label = { Text("Article / Description") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        trailingIcon = {
                            IconButton(onClick = { viewModel.triggerBarcodeScan("dechargementColisArticle") }) {
                                Icon(
                                    imageVector = Icons.Default.QrCodeScanner,
                                    contentDescription = "Scan",
                                    tint = Color(0xFF4F46E5)
                                )
                            }
                        }
                    )

                    OutlinedTextField(
                        value = colQty,
                        onValueChange = { viewModel.dechargementColisQuantite.value = it },
                        label = { Text("Quantité") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        trailingIcon = {
                            IconButton(onClick = { viewModel.triggerBarcodeScan("dechargementColisQuantite") }) {
                                Icon(
                                    imageVector = Icons.Default.QrCodeScanner,
                                    contentDescription = "Scan",
                                    tint = Color(0xFF4F46E5)
                                )
                            }
                        }
                    )

                    Button(
                        onClick = { viewModel.addDechargementItem() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Ajouter")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Ajouter au tableau")
                    }
                }
            }

            // Alert Feedbacks
            errorPrompt?.let {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Warning, contentDescription = "Attention", tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = it, color = MaterialTheme.colorScheme.onErrorContainer)
                    }
                }
            }

            successPrompt?.let {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFC8E6C9)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = "Check", tint = Color(0xFF2E7D32))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = it, color = Color(0xFF1B5E20), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // TABLEAU DÉTAILS DÉCHARGEMENT
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val totalUnloaded = activeItems.sumOf { it.quantite }
                        Text("Articles à décharger (${activeItems.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text("Total Qté: $totalUnloaded", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    }

                    if (activeItems.isEmpty()) {
                        Text(
                            "Aucun article déchargé pour le moment.",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    } else {
                        activeItems.forEach { item ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(12.dp)
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = "Article: ${item.article}", fontWeight = FontWeight.Bold)
                                        Text(text = "Quantité: ${item.quantite}", style = MaterialTheme.typography.bodySmall)
                                    }
                                    IconButton(onClick = { viewModel.removeDechargementItem(item) }) {
                                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Supprimer", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ACTIONS DE SORTIE
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        val reportText = Sharer.generateDechargementText(
                            nbl, 
                            targetColis.toIntOrNull() ?: 0, 
                            selectedMagasin, 
                            matriculeAgent, 
                            activeItems,
                            dechargementHeaderQty.toIntOrNull() ?: 1
                        )
                        Sharer.generateAndSharePdf(context, "dechargement_$nbl", "Rapport de Déchargement", reportText)
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = "PDF")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("PDF")
                }

                OutlinedButton(
                    onClick = {
                        val reportText = Sharer.generateDechargementText(
                            nbl, 
                            targetColis.toIntOrNull() ?: 0, 
                            selectedMagasin, 
                            matriculeAgent, 
                            activeItems,
                            dechargementHeaderQty.toIntOrNull() ?: 1
                        )
                        Sharer.sendToWhatsApp(context, reportText)
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("WhatsApp")
                }

                Button(
                    onClick = { viewModel.archiveActiveDechargement() },
                    modifier = Modifier.weight(1.2f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Archiver")
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    if (showAddWarehouseDialog) {
        AddWarehouseDialog(
            onDismiss = { showAddWarehouseDialog = false },
            onSave = { nom ->
                viewModel.addMagasin(nom)
                showAddWarehouseDialog = false
            }
        )
    }
}


// ==========================================
// 4. CONTROLE REMORQUE SCREEN (INTERFACE 3)
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControleRemorqueScreen(viewModel: LogisticsViewModel) {
    val context = LocalContext.current
    val inspecteur by viewModel.remorqueInspecteur.collectAsStateWithLifecycle()
    val matricule by viewModel.remorqueMatricule.collectAsStateWithLifecycle()
    val checklist by viewModel.remorqueChecklist.collectAsStateWithLifecycle()
    val remarques by viewModel.remorqueRemarques.collectAsStateWithLifecycle()
    val decision by viewModel.remorqueDecision.collectAsStateWithLifecycle()
    val errorMsg by viewModel.remorqueError.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Contrôle Remorque Vide") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.currentScreen.value = Screen.DASHBOARD }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFE58F00),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // INFORMATIONS GÉNÉRALES
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Informations Générales", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFFE58F00))

                    OutlinedTextField(
                        value = inspecteur,
                        onValueChange = { viewModel.remorqueInspecteur.value = it },
                        label = { Text("Nom de l'inspecteur") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = matricule,
                        onValueChange = { viewModel.remorqueMatricule.value = it },
                        label = { Text("N° Remorque (Matricule)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        trailingIcon = {
                            IconButton(onClick = { viewModel.triggerBarcodeScan("remorqueMatricule") }) {
                                Icon(
                                    imageVector = Icons.Default.QrCodeScanner,
                                    contentDescription = "Scan",
                                    tint = Color(0xFFE58F00)
                                )
                            }
                        }
                    )
                }
            }

            // POINTS DE CONTRÔLE (CHECKLIST)
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Points de Contrôle (Checklist)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFFE58F00))

                    viewModel.remorquePoints.forEachIndexed { index, point ->
                        val selectedStatus = checklist[point] ?: ""

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(text = "${index + 1}. $point", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)

                            // Segmented row OK / N_OK / NA
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                SegmentedButtonOption(
                                    label = "OK",
                                    isSelected = selectedStatus == "OK",
                                    selectedColor = Color(0xFF2E7D32),
                                    modifier = Modifier.weight(1f),
                                    onClick = { viewModel.setRemorqueCheckPoint(point, "OK") }
                                )

                                SegmentedButtonOption(
                                    label = "N_OK",
                                    isSelected = selectedStatus == "N_OK",
                                    selectedColor = Color(0xFFC62828),
                                    modifier = Modifier.weight(1f),
                                    onClick = { viewModel.setRemorqueCheckPoint(point, "N_OK") }
                                )

                                SegmentedButtonOption(
                                    label = "NA",
                                    isSelected = selectedStatus == "NA",
                                    selectedColor = Color(0xFF757575),
                                    modifier = Modifier.weight(1f),
                                    onClick = { viewModel.setRemorqueCheckPoint(point, "NA") }
                                )
                            }
                        }
                    }
                }
            }

            // DÉCISION ET REMARQUES
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Décision & Remarques", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFFE58F00))

                    OutlinedTextField(
                        value = remarques,
                        onValueChange = { viewModel.remorqueRemarques.value = it },
                        label = { Text("Remarques (Anomalies libres)") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )

                    // Decision Dropdown (Accepté / Refusé / Accepté sous réserve)
                    var expandedDecisionMenu by remember { mutableStateOf(false) }
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = decision,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Décision Finale") },
                            trailingIcon = {
                                IconButton(onClick = { expandedDecisionMenu = !expandedDecisionMenu }) {
                                    Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Ouvrir")
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        DropdownMenu(
                            expanded = expandedDecisionMenu,
                            onDismissRequest = { expandedDecisionMenu = false }
                        ) {
                            listOf("Accepté", "Refusé", "Accepté sous réserve").forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        viewModel.remorqueDecision.value = option
                                        expandedDecisionMenu = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Error block
            errorMsg?.let {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = it, color = MaterialTheme.colorScheme.onErrorContainer, modifier = Modifier.padding(12.dp))
                }
            }

            // ACTIONS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        val rep = Sharer.generateControleRemorqueText(inspecteur, matricule, viewModel.remorquePoints, checklist, remarques, decision)
                        Sharer.generateAndSharePdf(context, "controle_remorque_$matricule", "Fiche de Contrôle Remorque Vide", rep)
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = "PDF")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("PDF")
                }

                OutlinedButton(
                    onClick = {
                        val rep = Sharer.generateControleRemorqueText(inspecteur, matricule, viewModel.remorquePoints, checklist, remarques, decision)
                        Sharer.sendToWhatsApp(context, rep)
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("WhatsApp")
                }

                Button(
                    onClick = { viewModel.archiveControleRemorque() },
                    modifier = Modifier.weight(1.2f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                ) {
                    Text("Archiver")
                }
            }

            Button(
                onClick = { viewModel.resetControleRemorqueFields() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.outlineVariant)
            ) {
                Text("Réinitialiser les champs", color = MaterialTheme.colorScheme.onBackground)
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}


// ==========================================
// 5. CONTROLE EXPORT SCREEN (INTERFACE 4)
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControleExportScreen(viewModel: LogisticsViewModel) {
    val context = LocalContext.current
    val inspecteur by viewModel.exportInspecteur.collectAsStateWithLifecycle()
    val matricule by viewModel.exportMatricule.collectAsStateWithLifecycle()
    val checklist by viewModel.exportChecklist.collectAsStateWithLifecycle()
    val remarques by viewModel.exportRemarques.collectAsStateWithLifecycle()
    val decision by viewModel.exportDecision.collectAsStateWithLifecycle()
    val errorMsg by viewModel.exportError.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Contrôle Export") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.currentScreen.value = Screen.DASHBOARD }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0F8C79),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // INFORMATIONS GÉNÉRALES
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Informations Générales Export", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF0F8C79))

                    OutlinedTextField(
                        value = inspecteur,
                        onValueChange = { viewModel.exportInspecteur.value = it },
                        label = { Text("Nom de l'inspecteur") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = matricule,
                        onValueChange = { viewModel.exportMatricule.value = it },
                        label = { Text("N° Remorque (Matricule)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        trailingIcon = {
                            IconButton(onClick = { viewModel.triggerBarcodeScan("exportMatricule") }) {
                                Icon(
                                    imageVector = Icons.Default.QrCodeScanner,
                                    contentDescription = "Scan",
                                    tint = Color(0xFF0F8C79)
                                )
                            }
                        }
                    )
                }
            }

            // CHECKLIST D'EXPORT
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Checklist Pré-chargement Export", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF0F8C79))

                    viewModel.exportPoints.forEachIndexed { index, point ->
                        val selectedStatus = checklist[point] ?: ""

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(text = "${index + 1}. $point", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                SegmentedButtonOption(
                                    label = "OK",
                                    isSelected = selectedStatus == "OK",
                                    selectedColor = Color(0xFF2E7D32),
                                    modifier = Modifier.weight(1f),
                                    onClick = { viewModel.setExportCheckPoint(point, "OK") }
                                )

                                SegmentedButtonOption(
                                    label = "N_OK",
                                    isSelected = selectedStatus == "N_OK",
                                    selectedColor = Color(0xFFC62828),
                                    modifier = Modifier.weight(1f),
                                    onClick = { viewModel.setExportCheckPoint(point, "N_OK") }
                                )

                                SegmentedButtonOption(
                                    label = "NA",
                                    isSelected = selectedStatus == "NA",
                                    selectedColor = Color(0xFF757575),
                                    modifier = Modifier.weight(1f),
                                    onClick = { viewModel.setExportCheckPoint(point, "NA") }
                                )
                            }
                        }
                    }
                }
            }

            // DÉCISION ET REMARQUES
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Décision & Remarques Export", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF0F8C79))

                    OutlinedTextField(
                        value = remarques,
                        onValueChange = { viewModel.exportRemarques.value = it },
                        label = { Text("Remarques / Réserves") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )

                    var expandedDecisionMenu by remember { mutableStateOf(false) }
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = decision,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Décision Finale") },
                            trailingIcon = {
                                IconButton(onClick = { expandedDecisionMenu = !expandedDecisionMenu }) {
                                    Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Ouvrir")
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        DropdownMenu(
                            expanded = expandedDecisionMenu,
                            onDismissRequest = { expandedDecisionMenu = false }
                        ) {
                            listOf("Accepté", "Refusé", "Accepté sous réserve").forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        viewModel.exportDecision.value = option
                                        expandedDecisionMenu = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            errorMsg?.let {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = it, color = MaterialTheme.colorScheme.onErrorContainer, modifier = Modifier.padding(12.dp))
                }
            }

            // ACTIONS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        val rep = Sharer.generateControleExportText(inspecteur, matricule, viewModel.exportPoints, checklist, remarques, decision)
                        Sharer.generateAndSharePdf(context, "controle_export_$matricule", "Fiche de Contrôle Export", rep)
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = "PDF")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("PDF")
                }

                OutlinedButton(
                    onClick = {
                        val rep = Sharer.generateControleExportText(inspecteur, matricule, viewModel.exportPoints, checklist, remarques, decision)
                        Sharer.sendToWhatsApp(context, rep)
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("WhatsApp")
                }

                Button(
                    onClick = { viewModel.archiveControleExport() },
                    modifier = Modifier.weight(1.2f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                ) {
                    Text("Archiver")
                }
            }

            Button(
                onClick = { viewModel.resetControleExportFields() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.outlineVariant)
            ) {
                Text("Réinitialiser les champs", color = MaterialTheme.colorScheme.onBackground)
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}


// Segmented option helper with solid focus coloring
@Composable
fun SegmentedButtonOption(
    label: String,
    isSelected: Boolean,
    selectedColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .height(40.dp)
            .border(
                width = 1.dp,
                color = if (isSelected) selectedColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                shape = RoundedCornerShape(8.dp)
            ),
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) selectedColor.copy(alpha = 0.15f) else Color.Transparent
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text(
                text = label,
                color = if (isSelected) selectedColor else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}


// ==========================================
// 6. HISTORIQUE & ARCHIVES SCREEN
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoriqueScreen(viewModel: LogisticsViewModel) {
    val context = LocalContext.current
    val listLoadingArchives by viewModel.chargements.collectAsStateWithLifecycle()
    val listUnloadingArchives by viewModel.dechargements.collectAsStateWithLifecycle()
    val listCheckTrailerArchives by viewModel.controlesRemorques.collectAsStateWithLifecycle()
    val listCheckExportArchives by viewModel.controlesExports.collectAsStateWithLifecycle()

    var activeTab by remember { mutableStateOf(0) }
    var activeFilterQuery by remember { mutableStateOf("") }

    // Clicked history item for Dialog summary view
    var selectedLoadingItem by remember { mutableStateOf<ChargementArchive?>(null) }
    var selectedUnloadingItem by remember { mutableStateOf<DechargementArchive?>(null) }
    var selectedTrailerCheckItem by remember { mutableStateOf<ControleRemorqueArchive?>(null) }
    var selectedExportCheckItem by remember { mutableStateOf<ControleExportArchive?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historique & Archives") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.currentScreen.value = Screen.DASHBOARD }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    titleContentColor = MaterialTheme.colorScheme.onTertiary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onTertiary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            
            // Scrollable tabs for selecting historical types
            ScrollableTabRow(
                selectedTabIndex = activeTab,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                edgePadding = 16.dp
            ) {
                Tab(selected = activeTab == 0, onClick = { activeTab = 0; activeFilterQuery = "" }) {
                    Text("Chargements", modifier = Modifier.padding(14.dp), fontWeight = FontWeight.Bold)
                }
                Tab(selected = activeTab == 1, onClick = { activeTab = 1; activeFilterQuery = "" }) {
                    Text("Déchargements", modifier = Modifier.padding(14.dp), fontWeight = FontWeight.Bold)
                }
                Tab(selected = activeTab == 2, onClick = { activeTab = 2; activeFilterQuery = "" }) {
                    Text("Remorques Vides", modifier = Modifier.padding(14.dp), fontWeight = FontWeight.Bold)
                }
                Tab(selected = activeTab == 3, onClick = { activeTab = 3; activeFilterQuery = "" }) {
                    Text("Exports", modifier = Modifier.padding(14.dp), fontWeight = FontWeight.Bold)
                }
            }

            // Search Bar Filter
            OutlinedTextField(
                value = activeFilterQuery,
                onValueChange = { activeFilterQuery = it },
                label = { Text("Rechercher (Code, Matricule, Agent...)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                singleLine = true,
                leadingIcon = { Icon(imageVector = Icons.Default.List, contentDescription = "Search") }
            )

            // Render matching list
            Box(modifier = Modifier.weight(1f)) {
                when (activeTab) {
                    0 -> {
                        val filtered = listLoadingArchives.filter {
                            it.codeLivraison.contains(activeFilterQuery, ignoreCase = true) ||
                            it.matriculeAgent.contains(activeFilterQuery, ignoreCase = true) ||
                            it.magasin.contains(activeFilterQuery, ignoreCase = true)
                        }
                        HistoryGenericList(
                            items = filtered,
                            emptyMessage = "Aucun rapport de chargement archivé.",
                            onItemClick = { selectedLoadingItem = it },
                            onDelete = { viewModel.deleteChargement(it.id) },
                            titleBuilder = { "Livraison: ${it.codeLivraison}" },
                            detailsBuilder = { "Magasin: ${it.magasin} | Agent: ${it.matriculeAgent}" },
                            dateBuilder = { ModelSerializer.formatDate(it.date) }
                        )
                    }

                    1 -> {
                        val filtered = listUnloadingArchives.filter {
                            it.nbl.contains(activeFilterQuery, ignoreCase = true) ||
                            it.matriculeAgent.contains(activeFilterQuery, ignoreCase = true) ||
                            it.magasin.contains(activeFilterQuery, ignoreCase = true)
                        }
                        HistoryGenericList(
                            items = filtered,
                            emptyMessage = "Aucun rapport de déchargement archivé.",
                            onItemClick = { selectedUnloadingItem = it },
                            onDelete = { viewModel.deleteDechargement(it.id) },
                            titleBuilder = { "Bon NBL: ${it.nbl}" },
                            detailsBuilder = { "Magasin: ${it.magasin} | Agent: ${it.matriculeAgent}" },
                            dateBuilder = { ModelSerializer.formatDate(it.date) }
                        )
                    }

                    2 -> {
                        val filtered = listCheckTrailerArchives.filter {
                            it.matriculeRemorque.contains(activeFilterQuery, ignoreCase = true) ||
                            it.inspecteur.contains(activeFilterQuery, ignoreCase = true)
                        }
                        HistoryGenericList(
                            items = filtered,
                            emptyMessage = "Aucun contrôle de remorque archivé.",
                            onItemClick = { selectedTrailerCheckItem = it },
                            onDelete = { viewModel.deleteControleRemorque(it.id) },
                            titleBuilder = { "Remorque: ${it.matriculeRemorque}" },
                            detailsBuilder = { "Inspecteur: ${it.inspecteur} | Décision: ${it.decision}" },
                            dateBuilder = { ModelSerializer.formatDate(it.date) }
                        )
                    }

                    3 -> {
                        val filtered = listCheckExportArchives.filter {
                            it.matriculeRemorque.contains(activeFilterQuery, ignoreCase = true) ||
                            it.inspecteur.contains(activeFilterQuery, ignoreCase = true)
                        }
                        HistoryGenericList(
                            items = filtered,
                            emptyMessage = "Aucun contrôle d'export archivé.",
                            onItemClick = { selectedExportCheckItem = it },
                            onDelete = { viewModel.deleteControleExport(it.id) },
                            titleBuilder = { "Export Remorque: ${it.matriculeRemorque}" },
                            detailsBuilder = { "Inspecteur: ${it.inspecteur} | Décision: ${it.decision}" },
                            dateBuilder = { ModelSerializer.formatDate(it.date) }
                        )
                    }
                }
            }
        }
    }

    // Detail Dialog and Export blocks
    selectedLoadingItem?.let { item ->
        val itemsList = ModelSerializer.deserializeChargementItems(item.itemsJson)
        val reportTxt = Sharer.generateChargementText(item.codeLivraison, item.nombreColis, item.magasin, item.matriculeAgent, itemsList, item.article, item.quantite)
        ArchiveDetailsDialog(
            title = "Détails Chargement",
            formattedReport = reportTxt,
            filename = "chargement_${item.codeLivraison}",
            onDismiss = { selectedLoadingItem = null },
            whatsappSender = { Sharer.sendToWhatsApp(context, reportTxt) }
        )
    }

    selectedUnloadingItem?.let { item ->
        val itemsList = ModelSerializer.deserializeDechargementItems(item.itemsJson)
        val reportTxt = Sharer.generateDechargementText(item.nbl, item.nombreColis, item.magasin, item.matriculeAgent, itemsList, item.quantite)
        ArchiveDetailsDialog(
            title = "Détails Déchargement",
            formattedReport = reportTxt,
            filename = "dechargement_${item.nbl}",
            onDismiss = { selectedUnloadingItem = null },
            whatsappSender = { Sharer.sendToWhatsApp(context, reportTxt) }
        )
    }

    selectedTrailerCheckItem?.let { item ->
        val checklistMap = ModelSerializer.deserializeChecklist(item.pointsJson)
        val reportTxt = Sharer.generateControleRemorqueText(item.inspecteur, item.matriculeRemorque, viewModel.remorquePoints, checklistMap, item.remarques, item.decision)
        ArchiveDetailsDialog(
            title = "Détails Fiche Remorque",
            formattedReport = reportTxt,
            filename = "remorque_${item.matriculeRemorque}",
            onDismiss = { selectedTrailerCheckItem = null },
            whatsappSender = { Sharer.sendToWhatsApp(context, reportTxt) }
        )
    }

    selectedExportCheckItem?.let { item ->
        val checklistMap = ModelSerializer.deserializeChecklist(item.pointsJson)
        val reportTxt = Sharer.generateControleExportText(item.inspecteur, item.matriculeRemorque, viewModel.exportPoints, checklistMap, item.remarques, item.decision)
        ArchiveDetailsDialog(
            title = "Détails Fiche Export",
            formattedReport = reportTxt,
            filename = "export_${item.matriculeRemorque}",
            onDismiss = { selectedExportCheckItem = null },
            whatsappSender = { Sharer.sendToWhatsApp(context, reportTxt) }
        )
    }
}

@Composable
fun <T> HistoryGenericList(
    items: List<T>,
    emptyMessage: String,
    onItemClick: (T) -> Unit,
    onDelete: (T) -> Unit,
    titleBuilder: (T) -> String,
    detailsBuilder: (T) -> String,
    dateBuilder: (T) -> String
) {
    if (items.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(emptyMessage, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items) { it ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onItemClick(it) },
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(14.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = titleBuilder(it), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Text(text = detailsBuilder(it), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = "Archivé le: ${dateBuilder(it)}", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        }
                        IconButton(onClick = { onDelete(it) }) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Supprimer", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}


// Reusable popup for viewing detail text sheets & sharing
@Composable
fun ArchiveDetailsDialog(
    title: String,
    formattedReport: String,
    filename: String,
    onDismiss: () -> Unit,
    whatsappSender: () -> Unit
) {
    val context = LocalContext.current
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = "Fermer")
                    }
                }

                // Report Preview Box
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .verticalScroll(rememberScrollState())
                        .padding(12.dp)
                ) {
                    Text(
                        text = formattedReport,
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    )
                }

                // Sharing buttons inside modal
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            Sharer.generateAndSharePdf(context, filename, title, formattedReport)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "PDF")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("PDF Link")
                    }

                    Button(
                        onClick = whatsappSender,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)) // WhatsApp brand green
                    ) {
                        Text("WhatsApp")
                    }
                }
            }
        }
    }
}


// Simple warehouse dynamic input dialog
@Composable
fun AddWarehouseDialog(
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var txtVal by remember { mutableStateOf("") }
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Ajouter un nouveau magasin", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                
                OutlinedTextField(
                    value = txtVal,
                    onValueChange = { txtVal = it },
                    label = { Text("Nom du magasin / Dépôt") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Annuler")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { onSave(txtVal) }) {
                        Text("Ajouter")
                    }
                }
            }
        }
    }
}


// ==========================================
// BARCODE SCANNER DIALOG COMPOSABLE
// ==========================================
@Composable
fun BarcodeScannerDialog(viewModel: LogisticsViewModel) {
    val targetField by viewModel.barcodeScannerTarget.collectAsStateWithLifecycle()
    var manualCode by remember { mutableStateOf("") }

    // Depending on what we are scanning for, customize descriptions and presets
    val (targetName, presetCodes) = when (targetField) {
        "chargementCodeLivraison" -> Pair(
            "Code Livraison",
            listOf("LIV-90412-FR", "LIV-88214-DE", "LIV-30219-IT")
        )
        "chargementNombreColis" -> Pair(
            "Nombre de Colis (Chargement)",
            listOf("5", "10", "15")
        )
        "chargementMatriculeAgent" -> Pair(
            "Matricule Agent (Chargement)",
            listOf("CHAUF-TR76", "CHAUF-XD90", "CHAUF-LM02")
        )
        "chargementHeaderArticle" -> Pair(
            "Article Principal (Chargement)",
            listOf("MOTEUR-V8-SPORT", "BOITE-MANUELLE-6", "CARTERS-ALUM")
        )
        "chargementHeaderQuantite" -> Pair(
            "Quantité Principale (Chargement)",
            listOf("20", "50", "100")
        )
        "chargementColisNum" -> Pair(
            "Numéro Colis",
            listOf("COLIS-A9210", "COLIS-B8823", "COLIS-C1045")
        )
        "chargementColisArticle" -> Pair(
            "Article (Colis)",
            listOf("PLAQUETTES-FREIN-P1", "AMORTISSEUR-REB-20", "PNEUS-DE-RECHANGE")
        )
        "chargementColisQuantite" -> Pair(
            "Quantité (Colis)",
            listOf("1", "2", "5")
        )
        "dechargementNbl" -> Pair(
            "Numéro Bon de Livraison (NBL)",
            listOf("NBL-40120", "NBL-99321", "NBL-32049")
        )
        "dechargementNombreColis" -> Pair(
            "Nombre de Colis (Déchargement)",
            listOf("8", "12", "24")
        )
        "dechargementMatriculeAgent" -> Pair(
            "Matricule Agent (Déchargement)",
            listOf("RECEPT-92", "RECEPT-44", "RECEPT-10")
        )
        "dechargementHeaderQuantite" -> Pair(
            "Quantité Principale (Déchargement)",
            listOf("15", "30", "60")
        )
        "dechargementColisArticle" -> Pair(
            "Article (Réception)",
            listOf("BOITE-VITESSE-AUTO", "CHASSIS-MODULE-C7", "KIT-EMBRAYAGE-HQ")
        )
        "dechargementColisQuantite" -> Pair(
            "Quantité (Colis Réception)",
            listOf("1", "3", "6")
        )
        "remorqueMatricule" -> Pair(
            "N° Remorque (Contrôle Vide)",
            listOf("REM-882-EXPORT", "REM-904-EXPORT", "REM-107-EXPORT")
        )
        "exportMatricule" -> Pair(
            "N° Remorque (Contrôle Export)",
            listOf("REM-882-EXPORT", "REM-301-EXPORT", "REM-509-EXPORT")
        )
        else -> Pair("Donnée Générique", listOf("COD-88210", "COD-99432", "COD-10245"))
    }

    Dialog(onDismissRequest = { viewModel.barcodeScannerActive.value = false }) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            border = BorderStroke(1.dp, Color(0xFF334155))
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF2563EB)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCodeScanner,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Text(
                            text = "Lecteur Optique",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                    
                    Text(
                        text = "CIBLE: ${targetName.uppercase()}",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF94A3B8)
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                // Beautiful HUD Scan Camera simulation
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF020617))
                        .border(1.dp, Color(0xFF475569), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    // Corner targeting brackets (HUD photo-shoot feel)
                    Box(modifier = Modifier.fillMaxSize().padding(14.dp)) {
                        // Top Left Bracket
                        Box(modifier = Modifier.align(Alignment.TopStart).size(16.dp).border(width = 3.dp, color = Color(0xFF2563EB), shape = RoundedCornerShape(topStart = 4.dp)))
                        // Top Right Bracket
                        Box(modifier = Modifier.align(Alignment.TopEnd).size(16.dp).border(width = 3.dp, color = Color(0xFF2563EB), shape = RoundedCornerShape(topEnd = 4.dp)))
                        // Bottom Left Bracket
                        Box(modifier = Modifier.align(Alignment.BottomStart).size(16.dp).border(width = 3.dp, color = Color(0xFF2563EB), shape = RoundedCornerShape(bottomStart = 4.dp)))
                        // Bottom Right Bracket
                        Box(modifier = Modifier.align(Alignment.BottomEnd).size(16.dp).border(width = 3.dp, color = Color(0xFF2563EB), shape = RoundedCornerShape(bottomEnd = 4.dp)))
                    }

                    // Simulated matrix scan lines or background grid
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null,
                            tint = Color(0xFF334155),
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Recherche de code-barres (Caméra)...",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF475569)
                        )
                    }

                    // Laser scan line animating dynamically
                    LaserScanLine()
                }

                // Interactive Simulator Segment
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "SIMULATEUR DE SCAN RAPIDE :",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = Color(0xFF64748B)
                    )

                    presetCodes.forEach { code ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF1E293B), RoundedCornerShape(12.dp))
                                .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp))
                                .clickable {
                                    viewModel.onBarcodeScanned(code)
                                }
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.QrCode,
                                    contentDescription = null,
                                    tint = Color(0xFF38BDF8),
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = code,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                                    ),
                                    color = Color.White
                                )
                            }
                            
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFF2563EB).copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "SCANNER",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                                    color = Color(0xFF38BDF8)
                                )
                            }
                        }
                    }
                }

                // Manual backup textfield
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "OU OUTIL DE SAISIE MANUELLE :",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = Color(0xFF64748B)
                    )

                    OutlinedTextField(
                        value = manualCode,
                        onValueChange = { manualCode = it },
                        label = { Text("Tapez / Editez manuellement ici", color = Color(0xFF64748B)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color(0xFF2563EB),
                            focusedBorderColor = Color(0xFF2563EB),
                            unfocusedBorderColor = Color(0xFF334155)
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        trailingIcon = {
                            if (manualCode.trim().isNotEmpty()) {
                                IconButton(
                                    onClick = {
                                        viewModel.onBarcodeScanned(manualCode.trim())
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Submit",
                                        tint = Color(0xFF38BDF8)
                                    )
                                }
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Bottom actions
                TextButton(
                    onClick = { viewModel.barcodeScannerActive.value = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "ANNULER / FERMER LE SCAN",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFFEF4444)
                    )
                }
            }
        }
    }
}


// ==========================================
// SEPARATED ANIMATION OPTIMIZATIONS
// ==========================================
@Composable
fun PulsingStatusDot(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    Box(
        modifier = modifier
            .size(8.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(Color(0xFF22C55E).copy(alpha = pulseAlpha))
    )
}

@Composable
fun LaserScanLine(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "laser")
    val laserOffsetPerc by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser"
    )

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val verticalOffset = maxHeight * laserOffsetPerc
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .offset(y = verticalOffset)
                .background(Color(0xFFEF4444))
        )
    }
}
