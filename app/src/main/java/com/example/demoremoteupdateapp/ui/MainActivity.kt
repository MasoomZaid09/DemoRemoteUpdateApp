package com.example.demoremoteupdateapp.ui

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.demoremoteupdateapp.*
import com.example.demoremoteupdateapp.databinding.ActivityMainBinding
import com.example.demoremoteupdateapp.updateChecker.OnUpdateCheckListener
import com.example.demoremoteupdateapp.updateChecker.UpdateHelper
import com.example.downloadmanager.DownloadController
import com.google.android.material.snackbar.Snackbar

const val apkUrl = "https://storage.evozi.com/apk/dl/17/02/15/com.ludo.king_196.apk"

class MainActivity : AppCompatActivity(), OnUpdateCheckListener {

    companion object {
        const val PERMISSION_REQUEST_STORAGE = 0
    }

    lateinit var binding: ActivityMainBinding
    lateinit var downloadController: DownloadController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // to check updates are available or not
        val updateHelper = UpdateHelper(this)
        updateHelper.checkForUpdates(this)

    }

    override fun onUpdateCheckListener(updateUrl: String) {
        // initialize download controller instance
        // for now we use apkURl for testing
        downloadController = DownloadController(this, apkUrl)
        createAlertBox()
    }

    private fun createAlertBox(){

        val builder: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)

        builder.setMessage("Please update for better experience.")

        builder.setTitle("Updates Available!")

        builder.setIcon(R.drawable.info)
        builder.setCancelable(false)

        builder.setPositiveButton("Update",
            DialogInterface.OnClickListener { dialog: DialogInterface, which: Int ->
                // When the user click yes button dialog box also be cancelled
                // check storage permission granted if yes then start downloading file
                checkStoragePermission()
                dialog.cancel()
            } as DialogInterface.OnClickListener)

        builder.setNegativeButton("Cancel",
            DialogInterface.OnClickListener { dialog: DialogInterface, which: Int ->
                // If user click no then dialog box is cancelled.
                dialog.cancel()
            } as DialogInterface.OnClickListener)

        val alertDialog: AlertDialog = builder.create()

        // Show the Alert Dialog box
        alertDialog.show()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_STORAGE) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // start downloading
                downloadController.enqueueDownload()
            } else {
                // Permission request was denied.
                binding.mainLayout.showSnackbar(R.string.storage_permission_denied, Snackbar.LENGTH_SHORT)
            }
        }
    }
    private fun checkStoragePermission() {
        // Check if the storage permission has been granted
        if (checkSelfPermissionCompat(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            // start downloading
            downloadController.enqueueDownload()
        } else {
            // Permission is missing and must be requested.
            requestStoragePermission()
        }
    }
    private fun requestStoragePermission() {
        if (shouldShowRequestPermissionRationaleCompat(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            binding.mainLayout.showSnackbar(
                R.string.storage_access_required,
                Snackbar.LENGTH_INDEFINITE, R.string.ok
            ) {
                requestPermissionsCompat(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    PERMISSION_REQUEST_STORAGE
                )
            }
        } else {
            requestPermissionsCompat(
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_STORAGE
            )
        }
    }
}