package com.example.meriyaadein.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.widget.Toast
import com.example.meriyaadein.data.local.DiaryEntry
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

object PdfGenerator {

    fun generateDiaryPdf(context: Context, entry: DiaryEntry): File? {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size in points (approx)
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas
        val paint = Paint()

        // Margins
        val margin = 40f
        var yPosition = margin

        // Title
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 24f
        paint.color = Color.BLACK
        canvas.drawText(entry.title, margin, yPosition, paint)
        yPosition += 40f

        // Date & Mood
        paint.typeface = Typeface.DEFAULT
        paint.textSize = 14f
        paint.color = Color.GRAY
        val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
        val dateStr = dateFormat.format(Date(entry.date))
        val moodStr = "Mood: ${entry.mood.emoji} ${entry.mood.label}"
        
        canvas.drawText("$dateStr  |  $moodStr", margin, yPosition, paint)
        yPosition += 40f

        // Divider
        paint.color = Color.LTGRAY
        paint.strokeWidth = 1f
        canvas.drawLine(margin, yPosition, pageInfo.pageWidth - margin, yPosition, paint)
        yPosition += 30f

        // Content
        paint.color = Color.BLACK
        paint.textSize = 16f
        
        // Simple text wrapping
        val textWidth = pageInfo.pageWidth - (2 * margin)
        val words = entry.content.split(" ")
        var currentLine = ""
        
        for (word in words) {
            val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
            val measureWidth = paint.measureText(testLine)
            
            if (measureWidth < textWidth) {
                currentLine = testLine
            } else {
                canvas.drawText(currentLine, margin, yPosition, paint)
                yPosition += 24f // Line height
                currentLine = word
                
                // Check for page end (basic implementation: just stop or move to next page - for now single page or truncate)
                if (yPosition > pageInfo.pageHeight - margin) {
                    // In a real app, we would finishPage and startPage. 
                    // For simplicity in this iteration, we'll just stop drawing or let it clip.
                    break 
                }
            }
        }
        if (currentLine.isNotEmpty()) {
            canvas.drawText(currentLine, margin, yPosition, paint)
        }

        pdfDocument.finishPage(page)

        // Save file
        val fileName = "Diary_${entry.id}_${System.currentTimeMillis()}.pdf"
        // Saving to app-specific external storage which doesn't require permissions on newer Android
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)

        try {
            pdfDocument.writeTo(FileOutputStream(file))
            Toast.makeText(context, "PDF saved to ${file.absolutePath}", Toast.LENGTH_LONG).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, "Error generating PDF: ${e.message}", Toast.LENGTH_SHORT).show()
            return null
        } finally {
            pdfDocument.close()
        }

        return file
    }
}
