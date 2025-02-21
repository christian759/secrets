package com.example.secrets

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import java.io.File
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.file.Files

fun embedFileInImage(coverImage: Bitmap, secretFile: File, outputFileName: String) {
    try {
        // Read file bytes
        val fileData: ByteArray = Files.readAllBytes(secretFile.toPath())
        val fileSizeBytes = ByteBuffer.allocate(8).putLong(fileData.size.toLong()).array()

        // Convert Bitmap to ByteArray
        val byteArrayOutputStream = ByteArrayOutputStream()
        coverImage.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val imageData: ByteArray = byteArrayOutputStream.toByteArray()

        // Combine image data, file data, and file size
        val combinedData = imageData + fileData + fileSizeBytes

        // Save the new image with embedded file to external storage
        val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val outputFile = File(picturesDir, outputFileName)
        outputFile.writeBytes(combinedData)
        println("File embedded in image successfully and saved to ${outputFile.absolutePath}!")
    } catch (e: Exception) {
        println("Error embedding file in image: ${e.message}")
    }
}

fun extractFileFromImage(context: Context, imageFileName: String, outputFileName: String) {
    try {
        val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val imageWithFile = File(picturesDir, imageFileName)
        val combinedData = imageWithFile.readBytes()

        // Read the last 8 bytes to get the file size
        val fileSizeBytes = combinedData.copyOfRange(combinedData.size - 8, combinedData.size)
        val fileSize = ByteBuffer.wrap(fileSizeBytes).long

        if (fileSize <= 0 || fileSize > combinedData.size - 8) {
            println("Error: Invalid file size embedded in the image.")
            return
        }

        // Extract the hidden file data
        val fileData = combinedData.copyOfRange(combinedData.size - 8 - fileSize.toInt(), combinedData.size - 8)

        // Determine appropriate storage location based on file type
        val outputDir = when {
            outputFileName.endsWith(".jpg") || outputFileName.endsWith(".png") ->
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            outputFileName.endsWith(".pdf") || outputFileName.endsWith(".txt") || outputFileName.endsWith(".docx") ->
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            else -> Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        }

        // Save the extracted file
        val outputFile = File(outputDir, outputFileName)
        outputFile.writeBytes(fileData)
        println("File extracted successfully and saved to ${outputFile.absolutePath}!")
    } catch (e: Exception) {
        println("Error extracting file: ${e.message}")
    }
}
