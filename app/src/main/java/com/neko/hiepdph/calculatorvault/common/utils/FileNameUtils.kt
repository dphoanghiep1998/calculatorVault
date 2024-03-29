package com.neko.hiepdph.calculatorvault.common.utils

import android.annotation.TargetApi
import android.content.ContentValues
import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.documentfile.provider.DocumentFile
import com.google.common.io.Files.getNameWithoutExtension
import com.neko.hiepdph.calculatorvault.common.extensions.config
import com.neko.hiepdph.calculatorvault.config.EncryptionMode
import com.neko.hiepdph.calculatorvault.encryption.CryptoCore
import org.apache.commons.io.FileUtils.deleteQuietly
import java.io.*
import java.util.*
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream


object FileNameUtils {

    const val ALL_MIME_TYPES = "*/*"

    // construct a with an approximation of the capacity
    private val MIME_TYPES: HashMap<String, String> = HashMap(1 + (68 / 0.75).toInt())
    fun createFilename(title: String): String {
        val cleanFileName = title.replace("[\\\\><\"|*?'%:#/]".toRegex(), " ")
        var fileName = cleanFileName.trim { it <= ' ' }.replace(" +".toRegex(), " ")
        if (fileName.length > 127) fileName = fileName.substring(0, 127)
        return fileName
    }

    // encrypt == true -> encrypt or decrypt
    fun copyFileToAnotherLocation(
        context: Context,
        sourceLocation: File,
        targetLocation: File,
        progress: (value: Int, currentFile: File) -> Unit,
        finish: (currentFile: File, targetFile: File, isSuccess: Boolean) -> Unit,
    ) {
        val `in`: InputStream = FileInputStream(sourceLocation)
        val bufferIn = BufferedInputStream(`in`)
        val targetFile = if (!targetLocation.exists()) targetLocation
        else if (targetLocation.isFile) targetLocation
        else File(
            targetLocation, sourceLocation.name
        )
        try {

            val out: OutputStream? = getOutputStream(targetFile, context)
            val bufferOut = BufferedOutputStream(out)

            // Copy the bits from instream to outstream
            val buf = ByteArray(8192)
            var len: Int = 0

            while (bufferIn.read(buf).also { len = it } > 0) {
                bufferOut.write(buf, 0, len)
                progress(len, sourceLocation)
            }
            bufferOut.close()
            bufferIn.close()
            `in`.close()
            out?.close()
            finish(sourceLocation, targetFile, true)
        } catch (e: Exception) {
            finish(sourceLocation, targetFile, false)
        }

    }

    fun decryptFileToAnotherLocation(
        context: Context,
        sourceLocation: File,
        targetLocation: File,
        progress: (value: Int, currentFile: File) -> Unit,
        finish: (currentFile: File, targetFile: File, isSuccess: Boolean) -> Unit,
        encryptionMode: Int = EncryptionMode.HIDDEN,
    ) {
        val `in`: InputStream = FileInputStream(sourceLocation)
        val bufferIn = BufferedInputStream(`in`)
        val targetFile = if (!targetLocation.exists()) targetLocation
        else if (targetLocation.isFile) targetLocation
        else File(
            targetLocation, sourceLocation.name
        )
        val out: OutputStream? = getOutputStream(targetFile, context)

        // Copy the bits from instream to outstream
        val buf = ByteArray(8192)
        var len: Int = 0

        try {
            if (encryptionMode == EncryptionMode.HIDDEN) {
                val bufferOut = BufferedOutputStream(out)
                while (bufferIn.read(buf).also { len = it } > 0) {
                    bufferOut.write(buf, 0, len)
                    progress(len, sourceLocation)
                }
                bufferOut.close()
                bufferIn.close()
                `in`.close()
                out?.close()
                finish(sourceLocation, targetFile, true)
            } else {
                val secretKey =
                    CryptoCore.getSingleInstance().getSecretKey(context.config.secretKey)
                val cipher = CryptoCore.getSingleInstance().decrypt(secretKey)
                val bufferOut = BufferedOutputStream(out)
                val cipherInputStream = CipherInputStream(bufferIn, cipher)

                while (cipherInputStream.read(buf).also { len = it } > 0) {
                    bufferOut.write(buf, 0, len)
                    progress(len, sourceLocation)
                }
                cipherInputStream.close()
                bufferIn.close()
                `in`.close()
                bufferOut.close()
                out?.close()
                finish(sourceLocation, targetFile, true)
            }
        } catch (e: Exception) {
            finish(sourceLocation, targetFile, false)
        }
    }

    fun unLockFile(
        context: Context,
        sourceLocation: File,
        targetLocation: File,
        progress: (value: Int, currentFile: File) -> Unit,
        finish: (currentFile: File, targetFile: File,isSuccess:Boolean) -> Unit,
        encryptionMode: Int = EncryptionMode.HIDDEN,
    ) {
        val `in`: InputStream = FileInputStream(sourceLocation)
        val bufferIn = BufferedInputStream(`in`)
        val targetFile = if (!targetLocation.exists()) targetLocation
        else if (targetLocation.isFile) targetLocation
        else File(
            targetLocation, sourceLocation.name
        )
        val out: OutputStream? = getOutputStream(targetFile, context)

        // Copy the bits from instream to outstream
        val buf = ByteArray(8192)
        var len: Int = 0
        try {
            if (encryptionMode == EncryptionMode.ENCRYPTION) {
                val secretKey = CryptoCore.getSingleInstance().getSecretKey(context.config.secretKey)
                val decodedDataCipher = CryptoCore.getSingleInstance().decrypt(secretKey)
                val bufferOut = BufferedOutputStream(out)

                val cipherOutputStream = CipherOutputStream(bufferOut, decodedDataCipher)

                while (bufferIn.read(buf).also { len = it } > 0) {
                    cipherOutputStream.write(buf, 0, len)
                    progress(len, sourceLocation)
                }
                cipherOutputStream.close()
                bufferOut.close()
                out?.close()
                bufferIn.close()
                `in`.close()
            } else {
                val bufferOut = BufferedOutputStream(out)

                while (bufferIn.read(buf).also { len = it } > 0) {
                    bufferOut.write(buf, 0, len)
                    progress(len, sourceLocation)
                }
                bufferOut.close()
                `in`.close()
                bufferIn.close()
            }
            out?.close()
            finish(sourceLocation, targetFile,true)
        }catch (e:Exception){
            out?.close()
            finish(sourceLocation, targetFile,false)
        }

    }

    fun encryptFileToAnotherLocation(
        context: Context,
        sourceLocation: File,
        targetLocation: File,
        progress: (value: Int, currentFile: File) -> Unit,
        finish: (currentFile: File, targetFile: File, isSuccess: Boolean) -> Unit,
        encryptionMode: Int = EncryptionMode.HIDDEN,
    ) {
        val `in`: InputStream = FileInputStream(sourceLocation)
        val bufferIn = BufferedInputStream(`in`)
        val targetFile = if (!targetLocation.exists()) targetLocation
        else if (targetLocation.isFile) targetLocation
        else File(
            targetLocation, sourceLocation.name
        )
        val out: OutputStream? = getOutputStream(targetFile, context)

        // Copy the bits from instream to outstream
        val buf = ByteArray(8192)
        var len: Int = 0

        try {
            if (encryptionMode == EncryptionMode.HIDDEN) {
                val bufferOut = BufferedOutputStream(out)
                while (bufferIn.read(buf).also { len = it } > 0) {
                    bufferOut.write(buf, 0, len)
                    progress(len, sourceLocation)
                }
                bufferOut.close()
                bufferIn.close()
                `in`.close()
                out?.close()
                finish(sourceLocation, targetFile, true)
            } else {
                val secretKey =
                    CryptoCore.getSingleInstance().getSecretKey(context.config.secretKey)
                Log.d("TAG", "encryptFileToAnotherLocation: " + secretKey)
                val encodedDataCipher = CryptoCore.getSingleInstance().encrypt(secretKey)
                val bufferOut = BufferedOutputStream(out)
                val cipherOutputStream = CipherOutputStream(bufferOut, encodedDataCipher)

                while (bufferIn.read(buf).also { len = it } > 0) {
                    cipherOutputStream.write(buf, 0, len)
                    progress(len, sourceLocation)
                }
                cipherOutputStream.close()
                bufferOut.close()
                bufferIn.close()
                `in`.close()
                out?.close()
                finish(sourceLocation, targetFile, true)

            }

        } catch (e: Exception){
            finish(sourceLocation, targetFile, false)

        }


    }

    fun getOutputStream(
        target: File, context: Context
    ): OutputStream? {
        var outStream: OutputStream? = null
        // First try the normal way
        if (isWritable(target)) {
            // standard way
            outStream = FileOutputStream(target)
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // Storage Access Framework
                val targetDocument: DocumentFile = getDocumentFile(
                    target, false, context
                ) ?: return null
                outStream = context?.contentResolver?.openOutputStream(targetDocument.uri)
            } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
                // Workaround for Kitkat ext SD card
                return MediaStoreHack.getOutputStream(context, target.path)
            }
        }
        return outStream
    }


    fun isWritableNormalOrSaf(
        folder: File, c: Context
    ): Boolean {

        // Find a non-existing file in this directory.
        var i = 0
        var file: File
        do {
            val fileName = "text.txt" + ++i
            file = File(folder, fileName)
        } while (file.exists())

        // First check regular writability
        if (isWritable(file)) {
            return true
        }
        // Next check SAF writability.
        val document: DocumentFile = getDocumentFile(file, false, c) ?: return false
        // This should have created the file - otherwise something is wrong with access URL.
//        val result = document.canWrite() && file.exists()
        val result = document.canWrite()
        // Ensure that the dummy file is not remaining.
        deleteFile(file, c)
        return result
    }

    fun getExtension(path: String): String {
        val e = path.lastIndexOf(".")

        if (e < 0) {
            return ""
        } else {
            return path.substring(path.lastIndexOf(".") + 1)
        }
    }

    fun getExtension(file: File): String {
        return getExtension(file.absolutePath)
    }

    fun deleteFile(file: File, context: Context): Boolean {
        // First try the normal deletion.
        val fileDelete: Boolean = rmdir(file, context)
        if (file.delete() || fileDelete) return true
        // Try with Storage Access Framework.
        if (isOnExtSdCard(file, context)) {
            val document: DocumentFile? = getDocumentFile(file, false, context)
            return document?.delete() ?: false
        }

        return !file.exists()
    }

    private fun isOnExtSdCard(file: File, c: Context): Boolean {
        return getExtSdCardFolder(file, c) != null
    }

    private fun rmdir(file: File, context: Context): Boolean {
//        if (!file.exists()) return true
        val files = file.listFiles()
        if (files != null && files.size > 0) {
            for (child in files) {
                rmdir(child, context)
            }
        }

        // Try the normal way
        if (file.delete()) {
            return true
        }
        if (deleteQuietly(file)) return true

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val document: DocumentFile? = getDocumentFile(file, true, context)
            if (document != null && document.delete()) {
                return true
            }
        }

        // Try the Kitkat workaround.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val resolver = context.contentResolver
            val values = ContentValues()
            values.put(MediaStore.MediaColumns.DATA, file.absolutePath)
            resolver.insert(MediaStore.Files.getContentUri("external"), values)

            // Delete the created entry, such that content provider will delete the file.
            resolver.delete(
                MediaStore.Files.getContentUri("external"),
                MediaStore.MediaColumns.DATA + "=?",
                arrayOf(file.absolutePath)
            )
        }
        return !file.exists()
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    private fun getExtSdCardPaths(context: Context?): Array<String> {
        val paths: MutableList<String> = ArrayList()
        for (file in context?.getExternalFilesDirs("external")!!) {
            if (file != null && file != context.getExternalFilesDir("external")) {
                val index = file.absolutePath.lastIndexOf("/Android/data")
                if (index < 0) {
                    Log.w(
                        "getExtSdCardPaths", "Unexpected external file dir: " + file.absolutePath
                    )
                } else {
                    var path = file.absolutePath.substring(0, index)
                    try {
                        path = File(path).canonicalPath
                    } catch (e: IOException) {
                        // Keep non-canonical path.
                    }
                    paths.add(path)
                }
            }
        }
        if (paths.isEmpty()) paths.add("/storage/sdcard1")
        return paths.toTypedArray()
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private fun getExtSdCardFolder(
        file: File, context: Context?
    ): String? {
        val extSdPaths: Array<String> = getExtSdCardPaths(context)
        try {
            for (i in extSdPaths.indices) {
                if (file.canonicalPath.startsWith(extSdPaths[i])) {
                    return extSdPaths[i]
                }
            }
        } catch (e: IOException) {
            return null
        }
        return null
    }

    /**
     * Get a DocumentFile corresponding to the given file (for writing on ExtSdCard on Android 5). If
     * the file is not existing, it is created.
     *
     * @param file The file.
     * @param isDirectory flag indicating if the file should be a directory.
     * @return The DocumentFile
     */
    private fun getDocumentFile(
        file: File, isDirectory: Boolean, context: Context
    ): DocumentFile? {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) return DocumentFile.fromFile(file)
        val baseFolder: String? = getExtSdCardFolder(file, context)
        var originalDirectory = false
        if (baseFolder == null) {
            return null
        }
        var relativePath: String? = null
        try {
            val fullPath = file.canonicalPath
            if (baseFolder != fullPath) relativePath =
                fullPath.substring(baseFolder.length + 1) else originalDirectory = true
        } catch (e: IOException) {
            return null
        } catch (f: java.lang.Exception) {
            originalDirectory = true
            // continue
        }

//        val `as` = "Prefs.getString(PrefKeys.KEY_ACCESS_GRANT_STORAGE, null)"
        var treeUri: Uri? = null
//        if (`as` != null) treeUri = Uri.parse(`as`)
        treeUri = Uri.parse(file.path)
        if (treeUri == null) {
            return null
        }

        // start with root of SD card and then parse through document tree.
        var document = DocumentFile.fromTreeUri(context!!, treeUri)

        if (document == null) return null

        if (originalDirectory) return document
        val parts = relativePath!!.split("/")
        for (i in parts.indices) {
            var nextDocument = document!!.findFile(parts[i])
            if (nextDocument == null) {
                nextDocument = if (i < parts.size - 1 || isDirectory) {
                    document.createDirectory(parts[i])
                } else {
                    document.createFile("image", parts[i])
                }
            }
            document = nextDocument
        }
        return document
    }

    private fun isWritable(file: File?): Boolean {
        if (file == null) return false
        val isExisting = file.exists()
        try {
            val output = FileOutputStream(file, true)
            try {
                output.close()
            } catch (e: IOException) {
                e.printStackTrace()
                // do nothing.
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            return false
        }
        val result = file.canWrite()

        // Ensure that file is not created during this process.
        if (!isExisting) {
            file.delete()
        }
        return result
    }

    fun formatDurationTime(durationSeconds: Long): String {
        var hours = 0L
        var minutes = 0L
        var seconds = durationSeconds
        if (seconds >= 3600) {
            hours = seconds / 3600
            seconds -= hours * 3600
        }
        if (seconds >= 60) {
            minutes = seconds / 60
            seconds -= minutes * 60
        }
        return Formatter().format("%1\$02d:%2\$02d:%3\$02d", hours, minutes, seconds).toString()
    }

    fun getVideoDataSizeFromUrl(videoUrl: String): String? {
        try {
            val retriever = MediaMetadataRetriever()
            var durationMs: String? = "0"
            retriever.setDataSource(videoUrl, HashMap<String, String>())
            val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            durationMs = (duration?.toLong()?.div(1000))?.let { formatDurationTime(it) }
            retriever.release()
            return durationMs
        } catch (e: java.lang.Exception) {
            return null
        }

    }

    private fun createNewName(currentFolder: File, name: String, index: Int = 0): String {
        return if (currentFolder.listFiles()?.any { it.name == name } == true) {

            val newFile = File(currentFolder, name)
            val extension = getExtension(newFile.absolutePath)

            var newIndex = 1
            var newName =
                getNameWithoutExtension(newFile.absolutePath) + "($newIndex)" + if (TextUtils.isEmpty(
                        extension
                    )
                ) "" else ".$extension"


            val patttern = java.util.regex.Pattern.compile(".*\\([0-9]*\\)\\..*")

            if (patttern.matcher(name).find()) {
                val lastIndexClose = name.lastIndexOf(")")
                val lastIndexOpen = name.lastIndexOf("(")
                if (lastIndexOpen in 1 until lastIndexClose) {
                    val currentIndex =
                        name.substring(lastIndexOpen + 1, lastIndexClose).toIntOrNull() ?: 0

                    if (currentIndex == 0) {
                        newName =
                            getNameWithoutExtension(newFile.absolutePath) + "($newIndex)" + if (TextUtils.isEmpty(
                                    extension
                                )
                            ) "" else ".$extension"
                    } else {
                        newIndex = currentIndex + 1
                        newName = name.substring(
                            0, lastIndexOpen
                        ) + "($newIndex)" + if (TextUtils.isEmpty(extension)) "" else ".$extension"
                    }
                }
            }

            createNewName(currentFolder, newName, newIndex)
        } else {
            name
        }
    }

    fun createNewFile(
        context: Context, currentFolder: File, name: String, index: Int = 0
    ): String? {
        val newFile = File(currentFolder, createNewName(currentFolder, name))
//    newFile.createNewFile()

        // Try the normal way
        try {
            if (newFile.createNewFile()) {
                return newFile.path
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        Log.d("TAG", "createNewFile: ")
//        if (!isWritableNormalOrSaf(currentFolder, context)) {
//            Log.d("TAG", "createNewFile2: ")
//            throw Exception(currentFolder.path)
//        }


        // Try with Storage Access Framework.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val document: DocumentFile? = getDocumentFile(
                newFile.getParentFile(), true, context
            )
            // getDocumentFile implicitly creates the directory.
            try {
                if (document?.createFile(
                        getMimeType(newFile.path, newFile.isDirectory), newFile.name
                    ) != null

                ) {
                    Log.d("TAG", "createNewFile3: ")
                    return newFile.path
                }
            } catch (e: Exception) {
                e.printStackTrace()

            }
        }

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                mkfile(context, newFile)
                newFile.path

            } catch (e: Exception) {
                null
            }
        } else null
    }

    fun getMimeType(path: String, isDirectory: Boolean): String {
        if (isDirectory) {
            return null.toString()
        }
        var type: String? = ALL_MIME_TYPES
        val extension: String = getExtension(path)

        // mapping extension to system mime types
        if (extension != null && !extension.isEmpty()) {
            val extensionLowerCase = extension.lowercase(Locale.getDefault())
            val mime: MimeTypeMap = MimeTypeMap.getSingleton()
            type = mime.getMimeTypeFromExtension(extensionLowerCase)
            if (type == null) {
                type = MIME_TYPES.get(extensionLowerCase)
            }
        }
        if (type == null) type = ALL_MIME_TYPES
        return type
    }

    fun mkdir(context: Context, file: File?): Boolean {
        if (file == null) return false
        if (file.exists()) {
            // nothing to create.
            return file.isDirectory
        }

        // Try the normal way
        if (file.mkdirs()) {
            return true
        }

        // Try with Storage Access Framework.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && isOnExtSdCard(file, context)) {
            val document: DocumentFile? = getDocumentFile(file, true, context)
            // getDocumentFile implicitly creates the directory.
            return document != null && document.exists()
        }

        // Try the Kitkat workaround.
        return false
    }

    fun mkfile(context: Context, file: File): Boolean {
        val outputStream = getOutputStream(file, context) ?: return false
        try {
            outputStream.close()
            return true
        } catch (e: IOException) {
        }
        return false
    }
}