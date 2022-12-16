package com.example.downloadmanager

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.DownloadManager.Query
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.demoremoteupdateapp.BuildConfig
import com.example.demoremoteupdateapp.R
import java.io.File

const val TAG = "OTA TESTING"

class DownloadController(private val context: Context, private val url: String) {

    companion object {
        private const val FILE_NAME = "SampleDownloadApp.apk"
        private const val FILE_BASE_PATH = "file://"
        private const val MIME_TYPE = "application/vnd.android.package-archive"
        private const val PROVIDER_PATH = ".provider"
        private const val APP_INSTALL_PATH = "\"application/vnd.android.package-archive\""
    }

    lateinit var downloadManager: DownloadManager
    var downloadId = 0L

    fun enqueueDownload() {

        var destination =
            context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/"
        destination += FILE_NAME

        val uri = Uri.parse("$FILE_BASE_PATH$destination")

        val file = File(destination)
        if (file.exists()) file.delete()

        downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadUri = Uri.parse(url)
        val request = DownloadManager.Request(downloadUri).apply {
            setMimeType(MIME_TYPE)
//                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION)
            setTitle("Ludo King.apk")
            setDescription(context.getString(R.string.downloading))
        }

        // set destination
        request.setDestinationUri(uri)
        Log.i(TAG, uri.toString())


        showInstallOption(destination, uri)
        // Enqueue a new download and same the referenceId
        downloadId = downloadManager.enqueue(request)

//        downloadManager.remove(downloadId)

//        // for handle status
//        val downloadQuery = DownloadManager.Query()
//        //set the query filter to our previously Enqueued download
//        downloadQuery.setFilterById(downloadId)
//
//        //Query the download manager about downloads that have been requested.
//        val cursor: Cursor = downloadManager.query(downloadQuery)
//        if (cursor.moveToFirst()) {
//        //  DownloadStatus(cursor, downloadId,destination, uri)
//        }

    }


//    private fun DownloadStatus(cursor: Cursor, DownloadId: Long, destination: String, uri: Uri) {
//
//        //column for download  status
//        val columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
//        val status = cursor.getInt(columnIndex)
//        //column for reason code if the download failed or paused
//        val columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON)
//        val reason = cursor.getInt(columnReason)
//        //get the download filename
//        val filenameIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)
//        val filename = cursor.getString(filenameIndex)
//        var statusText = ""
//        var reasonText = ""
//
//        when (status) {
//            DownloadManager.STATUS_FAILED -> {
//                statusText = "STATUS_FAILED"
//                when (reason) {
//                    DownloadManager.ERROR_CANNOT_RESUME -> reasonText = "ERROR_CANNOT_RESUME"
//                    DownloadManager.ERROR_DEVICE_NOT_FOUND -> reasonText = "ERROR_DEVICE_NOT_FOUND"
//                    DownloadManager.ERROR_FILE_ALREADY_EXISTS -> reasonText =
//                        "ERROR_FILE_ALREADY_EXISTS"
//                    DownloadManager.ERROR_FILE_ERROR -> reasonText = "ERROR_FILE_ERROR"
//                    DownloadManager.ERROR_HTTP_DATA_ERROR -> reasonText = "ERROR_HTTP_DATA_ERROR"
//                    DownloadManager.ERROR_INSUFFICIENT_SPACE -> reasonText =
//                        "ERROR_INSUFFICIENT_SPACE"
//                    DownloadManager.ERROR_TOO_MANY_REDIRECTS -> reasonText =
//                        "ERROR_TOO_MANY_REDIRECTS"
//                    DownloadManager.ERROR_UNHANDLED_HTTP_CODE -> reasonText =
//                        "ERROR_UNHANDLED_HTTP_CODE"
//                    DownloadManager.ERROR_UNKNOWN -> reasonText = "ERROR_UNKNOWN"
//                }
//            }
//            DownloadManager.STATUS_PAUSED -> {
//                statusText = "STATUS_PAUSED"
//                when (reason) {
//                    DownloadManager.PAUSED_QUEUED_FOR_WIFI -> reasonText = "PAUSED_QUEUED_FOR_WIFI"
//                    DownloadManager.PAUSED_UNKNOWN -> reasonText = "PAUSED_UNKNOWN"
//                    DownloadManager.PAUSED_WAITING_FOR_NETWORK -> reasonText =
//                        "PAUSED_WAITING_FOR_NETWORK"
//                    DownloadManager.PAUSED_WAITING_TO_RETRY -> reasonText =
//                        "PAUSED_WAITING_TO_RETRY"
//                }
//            }
//            DownloadManager.STATUS_PENDING -> statusText = "STATUS_PENDING"
//            DownloadManager.STATUS_RUNNING ->{
//                statusText = "STATUS_RUNNING"
//                showInstallOption(destination, uri)
//            }
//            DownloadManager.STATUS_SUCCESSFUL -> {
////                showInstallOption(destination, uri)
//                statusText = "STATUS_SUCCESSFUL"
//                reasonText = "Filename:\n$filename"
//            }
//        }
////        if (DownloadId == Music_DownloadId) {
////            val toast = Toast.makeText(
////                this@MainActivity,
////                """
////                  Music Download Status:
////                  $statusText
////                  $reasonText
////                  """.trimIndent(),
////                Toast.LENGTH_LONG
////            )
////            toast.setGravity(Gravity.TOP, 25, 400)
////            toast.show()
////        } else {
////            val toast = Toast.makeText(
////                this@MainActivity,
////                """
////                  Image Download Status:
////                  $statusText
////                  $reasonText
////                  """.trimIndent(),
////                Toast.LENGTH_LONG
////            )
////            toast.setGravity(Gravity.TOP, 25, 400)
////            toast.show()
//
////            Log.i(TAG,reasonText)
////            Log.i(TAG,statusText)
//
//            // Make a delay of 3 seconds so that next toast (Music Status) will not merge with this one.
//            CoroutineScope(Dispatchers.Main).launch {
//                delay(3000L)
//            }
//        }

    private fun showInstallOption(
        destination: String,
        uri: Uri
    ) {

        // set BroadcastReceiver to install app when .apk is downloaded
        val onComplete = object : BroadcastReceiver() {
            @SuppressLint("Range")
            override fun onReceive(
                context: Context,
                intent: Intent
            ) {

                val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (downloadId == id) {

                    val action = intent.action
                    if (action == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
                        val query = Query()
                        query.setFilterById(
                            intent.getLongExtra(
                                DownloadManager.EXTRA_DOWNLOAD_ID,
                                0
                            )
                        )

                        val cursor = downloadManager.query(query)
                        if (cursor.moveToFirst()) {

                            // if download completed...
                            if (cursor.count > 0) {
                                val status =
                                    cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                                if (status == DownloadManager.STATUS_SUCCESSFUL) {
                                    // So something here on success

                                    Log.i(TAG, "successed")

//                                    var downloadFilePath: String? = null
//                                    val downloadFileLocalUri =
//                                        cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))
//                                    if (downloadFileLocalUri != null) {
//                                        val mFile = File(Uri.parse(downloadFileLocalUri).path)
//                                        downloadFilePath = mFile.absolutePath
//                                    }

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        val contentUri = FileProvider.getUriForFile(
                                            context,
                                            BuildConfig.APPLICATION_ID + PROVIDER_PATH,
                                            File(destination)
                                        )
                                        val install = Intent(Intent.ACTION_VIEW)
                                        install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                        install.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                        install.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true)
                                        install.data = contentUri
                                        context.startActivity(install)
                                        context.unregisterReceiver(this)
                                        // finish()
                                    } else {
                                        val install = Intent(Intent.ACTION_VIEW)
                                        install.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                        install.setDataAndType(uri, APP_INSTALL_PATH)
                                        context.startActivity(install)
                                        context.unregisterReceiver(this)
                                        // finish()
                                    }


                                } else {

                                    Log.i(TAG, "Failed")
                                    // So something here on failed.
                                    Toast.makeText(context, "failed", Toast.LENGTH_SHORT).show()
                                }
                            }

                        } else {
                            // if download cancelled..
//                            val message = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_REASON))
                            Toast.makeText(context, "Download Cancelled...", Toast.LENGTH_SHORT).show()
                            downloadManager.remove(downloadId)
                        }
                    }

                } else {
                    Toast.makeText(context, "Download is cancelled", Toast.LENGTH_SHORT).show()
                }
            }
        }
        context.registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }

//    private fun handleDownloadCompleteIntent(data: Intent,destination: String,uri: Uri) {
//        if (data.hasExtra(DownloadManager.EXTRA_DOWNLOAD_ID)) {
////            val id = data.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
////            if (id != -1L) {
//                Toast.makeText(context, "Download is cancelled", Toast.LENGTH_SHORT).show()
//                downloadManager.remove(downloadId)
//            }else{
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                    val contentUri = FileProvider.getUriForFile(
//                        context,
//                        BuildConfig.APPLICATION_ID + PROVIDER_PATH,
//                        File(destination)
//                    )
//                    val install = Intent(Intent.ACTION_VIEW)
//                    install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//                    install.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                    install.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true)
//                    install.data = contentUri
//                    context.startActivity(install)
//                    // finish()
//                }
//                else {
//                    val install = Intent(Intent.ACTION_VIEW)
//                    install.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//                    install.setDataAndType(
//                        uri,
//                        APP_INSTALL_PATH
//                    )
//                    context.startActivity(install)
//                    // finish()
//                }
//            }
//        }
//    }


}

