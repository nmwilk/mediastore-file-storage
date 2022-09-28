package com.nmwilkinson.mediastorefiles

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.os.SystemClock
import android.provider.MediaStore
import android.provider.MediaStore.Files.FileColumns
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import logcat.LogPriority
import logcat.logcat
import kotlin.random.Random

class FilesAdapter(context: Context) : RecyclerView.Adapter<FilesAdapter.FileViewHolder>() {
    private val resolver: ContentResolver = context.contentResolver
    private val random = Random(SystemClock.currentThreadTimeMillis())
    private val fakeData = (0..1000).joinToString("") { "$it" }.toByteArray()
    private val appFolder = "Documents/MyAppFolder"
    private var data = listOf<String>()

    private val primaryUri = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

    fun scanDirectory() {
        val selection = FileColumns.RELATIVE_PATH + " like ? "
        val selectionArgs = arrayOf("%${appFolder}%")
        val projection = arrayOf(FileColumns.DISPLAY_NAME)
        val cursor = resolver.query(
            primaryUri,
            projection,
            selection,
            selectionArgs,
            null
        )

        logcat(LogPriority.INFO) { "got cursor? ${cursor != null}" }

        data = cursor?.let { c ->
            generateSequence { if (c.moveToNext()) c else null }
                .map { c.getString(0) }
                .toList()
        } ?: listOf()

        cursor?.close()

        logcat(LogPriority.INFO) { "data now: $data" }
        updateList()
    }

    @SuppressLint("NotifyDataSetChanged") // demo app, don't care
    private fun updateList() {
        notifyDataSetChanged()
    }

    fun createFile() {
        val filename = "file${random.nextInt(100)}.txt"
        val values = ContentValues().apply {
            put(FileColumns.DISPLAY_NAME, filename)
            put(FileColumns.MIME_TYPE, "text/plain")
            put(FileColumns.RELATIVE_PATH, appFolder)
            put(FileColumns.IS_PENDING, 1)
        }

        val uri = resolver.insert(primaryUri, values)

        logcat(LogPriority.INFO) { "got uri? ${uri != null}" }
        uri?.let {
            logcat(LogPriority.INFO) { "writing to $uri" }
            resolver.openOutputStream(uri)?.use { it.write(fakeData) }

            values.clear()
            values.put(MediaStore.Downloads.IS_PENDING, 0)
            resolver.update(uri, values, null, null)
        } ?: logcat(LogPriority.ERROR) { "Failed to insert file [$filename] in [$appFolder]" }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val textView = TextView(parent.context)
        textView.setPadding(20, 5, 20, 5)
        return FileViewHolder(textView)
    }

    class FileViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        holder.textView.text = data[position]
    }

    override fun getItemCount(): Int = data.size
}