package com.example.utils

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.*
import java.io.File
import java.io.FileOutputStream

object Sharer {

    // ==========================================
    // 1. FORMATTED TEXT GENERATORS
    // ==========================================

    fun generateChargementText(
        code: String,
        targetColis: Int,
        magasin: String,
        agent: String,
        items: List<ChargementItem>
    ): String {
        val totalQty = items.sumOf { it.quantite }
        val sb = StringBuilder()
        sb.append("📋 *RAPPORT DE CHARGEMENT STOCK*\n")
        sb.append("-------------------------------------------\n")
        sb.append("🔹 *Code Livraison :* $code\n")
        sb.append("🔹 *Colis attendus :* $targetColis\n")
        sb.append("🔹 *Colis scannés/saisis :* ${items.size}\n")
        sb.append("🔹 *Magasin Stockage :* $magasin\n")
        sb.append("🔹 *Matricule Agent :* $agent\n")
        sb.append("🔹 *Total Articles :* $totalQty\n\n")
        sb.append("📦 *Détails des Colis :*\n")
        if (items.isEmpty()) {
            sb.append("_Aucun colis dans la liste._\n")
        } else {
            items.forEachIndexed { idx, it ->
                sb.append("${idx + 1}. Numéro Colis : *${it.numeroColis}* | Article : *${it.article}* (Qté: *${it.quantite}*)\n")
            }
        }
        sb.append("\n-------------------------------------------")
        return sb.toString()
    }

    fun generateDechargementText(
        nbl: String,
        targetColis: Int,
        magasin: String,
        agent: String,
        items: List<DechargementItem>
    ): String {
        val totalQty = items.sumOf { it.quantite }
        val sb = StringBuilder()
        sb.append("📋 *RAPPORT DE DÉCHARGEMENT STOCK*\n")
        sb.append("-------------------------------------------\n")
        sb.append("🔸 *Numéro Bon de Livraison (NBL) :* $nbl\n")
        sb.append("🔸 *Colis attendus :* $targetColis\n")
        sb.append("🔸 *Articles déchargés :* ${items.size}\n")
        sb.append("🔸 *Magasin Stockage :* $magasin\n")
        sb.append("🔸 *Matricule Agent :* $agent\n")
        sb.append("🔸 *Quantité Totale :* $totalQty\n\n")
        sb.append("📦 *Détails des Articles :*\n")
        if (items.isEmpty()) {
            sb.append("_Aucun article déchargé._\n")
        } else {
            items.forEachIndexed { idx, it ->
                sb.append("${idx + 1}. Article : *${it.article}* | Quantité : *${it.quantite}*\n")
            }
        }
        sb.append("\n-------------------------------------------")
        return sb.toString()
    }

    fun generateControleRemorqueText(
        inspecteur: String,
        matricule: String,
        points: List<String>,
        checklist: Map<String, String>,
        remarques: String,
        decision: String
    ): String {
        val sb = StringBuilder()
        sb.append("🚛 *FICHE DE CONTRÔLE REMORQUE VIDE*\n")
        sb.append("-------------------------------------------\n")
        sb.append("👤 *Inspecteur :* $inspecteur\n")
        sb.append("🚛 *N° Remorque :* $matricule\n")
        sb.append("📌 *Décision Finale :* *${decision.uppercase(java.util.Locale.getDefault())}*\n\n")
        sb.append("✅ *Points de Contrôle :*\n")
        points.forEachIndexed { idx, pt ->
            val status = checklist[pt] ?: "N/D"
            val statusEmoji = when (status) {
                "OK" -> "🟩 OK"
                "N_OK" -> "🟥 NON OK"
                "NA" -> "🟨 N/A"
                else -> "▫️ Non Défini"
            }
            sb.append("${idx + 1}. $pt : *$statusEmoji*\n")
        }
        sb.append("\n✍️ *Remarques / Anomalies :*\n")
        if (remarques.trim().isEmpty()) {
            sb.append("_Aucune anomalie signalée._\n")
        } else {
            sb.append("$remarques\n")
        }
        sb.append("\n-------------------------------------------")
        return sb.toString()
    }

    fun generateControleExportText(
        inspecteur: String,
        matricule: String,
        points: List<String>,
        checklist: Map<String, String>,
        remarques: String,
        decision: String
    ): String {
        val sb = StringBuilder()
        sb.append("🌐 *FICHE DE CONTRÔLE EXPORT AVANT CHARGEMENT*\n")
        sb.append("-------------------------------------------\n")
        sb.append("👤 *Inspecteur :* $inspecteur\n")
        sb.append("🚛 *N° Remorque :* $matricule\n")
        sb.append("📌 *Décision Finale :* *${decision.uppercase(java.util.Locale.getDefault())}*\n\n")
        sb.append("🔍 *Checklist d'Export :*\n")
        points.forEachIndexed { idx, pt ->
            val status = checklist[pt] ?: "N/D"
            val statusEmoji = when (status) {
                "OK" -> "🟩 OK"
                "N_OK" -> "🟥 NON OK"
                "NA" -> "🟨 N/A"
                else -> "▫️ Non-défini"
            }
            sb.append("${idx + 1}. $pt : *$statusEmoji*\n")
        }
        sb.append("\n✍️ *Remarques / Anomalies :*\n")
        if (remarques.trim().isEmpty()) {
            sb.append("_Aucune anomalie signalée._\n")
        } else {
            sb.append("$remarques\n")
        }
        sb.append("\n-------------------------------------------")
        return sb.toString()
    }


    // ==========================================
    // 2. WHATSAPP SENDER
    // ==========================================

    fun sendToWhatsApp(context: Context, text: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
            setPackage("com.whatsapp")
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            // General share sheet chooser fallback
            val chooser = Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, text)
            }, "Partager via WhatsApp")
            context.startActivity(chooser)
        }
    }


    // ==========================================
    // 3. NATIVE PDF REPORT EXPORTER
    // ==========================================

    fun generateAndSharePdf(context: Context, filename: String, title: String, contentText: String) {
        val document = PdfDocument()
        
        // standard page width/height (A4 size: 595 x 842 points)
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = document.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        val paint = Paint()
        
        // Draw elegant corporate branding header
        paint.color = Color.parseColor("#1B365D") // Logistics Dark Blue
        canvas.drawRect(0f, 0f, 595f, 90f, paint)

        paint.color = Color.WHITE
        paint.textSize = 20f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("SUIVI LOGISTIQUE - RAPPORT", 30f, 40f, paint)

        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
        canvas.drawText("Généré le ${ModelSerializer.formatDate(System.currentTimeMillis())}", 30f, 65f, paint)

        // Draw secondary header bar
        paint.color = Color.parseColor("#E5A93B") // Safety Yellow/Orange Highlight
        canvas.drawRect(0f, 90f, 595f, 96f, paint)

        // Reset text drawing paint
        paint.color = Color.BLACK
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.textSize = 12f

        var yPos = 140f
        val margin = 40f
        val lines = contentText.split("\n")

        for (line in lines) {
            if (yPos > 800) {
                // Too long, let's stop or draw on a secondary page
                break
            }
            
            val cleanLine = line.replace("*", "").replace("_", "")
            
            if (line.trim().startsWith("---") || line.trim().startsWith("___")) {
                paint.color = Color.GRAY
                canvas.drawLine(margin, yPos, 595f - margin, yPos, paint)
                yPos += 20f
                paint.color = Color.BLACK
                continue
            }

            if (line.contains("RAPPORT") || line.contains("FICHE") || line.contains("Détails") || line.contains("Points") || line.contains("Checklist")) {
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                paint.textSize = 14f
                paint.color = Color.parseColor("#1B365D")
                canvas.drawText(cleanLine, margin, yPos, paint)
                yPos += 25f
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                paint.textSize = 11f
                paint.color = Color.BLACK
                continue
            }

            canvas.drawText(cleanLine, margin, yPos, paint)
            yPos += 20f
        }

        document.finishPage(page)

        // Save PDF to App Cache
        val cacheDir = File(context.cacheDir, "reports")
        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }
        val file = File(cacheDir, "$filename.pdf")
        
        try {
            val fos = FileOutputStream(file)
            document.writeTo(fos)
            document.close()
            fos.close()
            
            // Share via FileProvider Authority "com.example.fileprovider"
            val uri = FileProvider.getUriForFile(context, "com.example.fileprovider", file)
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(shareIntent, "Exporter le rapport PDF"))
            
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Erreur d'exportation PDF: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
