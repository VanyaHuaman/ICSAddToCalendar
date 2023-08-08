package com.example.icsdownload

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.util.UUID


object ICSDownloader {
    const val MIME_TYPE_ICS = "text/calendar"
    private const val ICS_FILE = ".ics"
    private const val BEGIN = "BEGIN"

    fun parseICSString(icsString: String?): String {
        val decodedUri = Uri.decode(icsString)
        val decodedUriString = decodedUri.toString()
        val start = decodedUriString.substringBefore(BEGIN)
        return if (!start.isNullOrBlank()) {
            decodedUriString.substringAfter(start).trim()
        } else {
            decodedUriString.trim()
        }
    }

    fun saveICSFile(icsString: String, context: Context) {
        val text = parseICSString(icsString)
        val fileName = UUID.randomUUID().toString() + ICS_FILE
        val directory = File(context.getExternalFilesDir(null), "saved_files")
        if (!directory.isDirectory) {
            directory.mkdir()
        }

        val file = File(directory, fileName)
        val outputStream = FileOutputStream(file)
        val outputWriter = OutputStreamWriter(outputStream)

        outputWriter.write(text)
        outputWriter.flush()
        outputWriter.close()
        outputStream.close()
        openFile(context, fileName)
    }

    private fun openFile(activityContext: Context?, downloadFileName: String) {
        activityContext?.let { theContext ->
            getContentUri(theContext, downloadFileName)?.let { fileUri ->
                val intent = Intent(Intent.ACTION_VIEW)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    .setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    .setDataAndType(fileUri, MIME_TYPE_ICS)
                try {
                    activityContext?.startActivity(intent)
                } catch (e: Exception) {
                    if (e is ActivityNotFoundException) {
                        Log.d(
                            "OpenFile",
                            "No associated File Viewer for mimeType: $MIME_TYPE_ICS"
                        )
                    } else {
                        Log.e("OpenFile", "Exception:$e")
                    }

                }
            }
        }
    }

    fun getFileUri(context: Context, downloadFileName: String): Uri? {
        val path = File(context.getExternalFilesDir(null), "saved_files")
        val file = File(path, downloadFileName)
        return Uri.fromFile(file)
    }

    fun getContentUri(context: Context, downloadFileName: String): Uri? {
        val path = File(context.getExternalFilesDir(null), "saved_files")
        val file = File(path, downloadFileName)
        return FileProvider.getUriForFile(context, "com.example.icsdownload.FileProvider", file)
    }
}