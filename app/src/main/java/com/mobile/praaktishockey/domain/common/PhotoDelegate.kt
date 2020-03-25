package com.mobile.praaktishockey.domain.common

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.mobile.praaktishockey.R
import com.mobile.praaktishockey.domain.extension.showAlert
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Observable


class PhotoDelegate {
    private val IMAGE_TYPE = "image/*"
    private val CONTENT_VALUE = "clientPhoto"
    private val host: PermissionsHost
    var capturedImageURI: Uri? = null
    val REQUEST_CAMERA = 234
    val REQUEST_GALLERY = 345
    private val CHOOSER_TITLE = ""
    val rxPermissions: Observable<Boolean> by lazy {
        RxPermissions(getContextFromHost())
            .request(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
    }

    var cameraCallback: ((Uri?) -> Unit)? = null
    var galleryCallback: ((Uri?) -> Unit)? = null

    constructor(context: Activity) {
        host = PermissionsHost.ActivitySeal(context)

    }

    constructor(frag: Fragment) {
        host = PermissionsHost.FragmentSeal(frag)
    }


    fun showChooserAlert(callback: (Uri?) -> Unit) {
        val contextFromHost = getContextFromHost()
        contextFromHost.showAlert {
            setTitle("Choose image")
            setCancelable(true)
            setItems(context.resources.getStringArray(R.array.entryvalues_choose_image)) { _, which ->
                when (which) {
                    0 -> galleryIntent(callback)
                    1 -> cameraIntent(callback)
                }
            }
        }
    }

    fun cameraIntent(callback: (Uri?) -> Unit) {
        this.cameraCallback = callback
        rxPermissions
            .subscribe {
                if (it) {
                    val values = ContentValues()
                    values.put(MediaStore.Images.Media.TITLE, CONTENT_VALUE)
                    capturedImageURI = getContextFromHost().contentResolver.insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        values
                    )
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageURI)
                    when (host) {
                        is PermissionsHost.ActivitySeal -> host.activity.startActivityForResult(intent, REQUEST_CAMERA)
                        is PermissionsHost.FragmentSeal -> host.frag.startActivityForResult(intent, REQUEST_CAMERA)
                    }

                }
            }
    }

    fun galleryIntent(callback: (Uri?) -> Unit) {
        this.galleryCallback = callback
        rxPermissions
            .subscribe {
                if (it) {
                    val intent = Intent().apply { type = IMAGE_TYPE; action = Intent.ACTION_GET_CONTENT }
                    when (host) {
                        is PermissionsHost.ActivitySeal -> host.activity.startActivityForResult(
                            Intent.createChooser(
                                intent,
                                CHOOSER_TITLE
                            ), REQUEST_GALLERY
                        )
                        is PermissionsHost.FragmentSeal -> host.frag.startActivityForResult(
                            Intent.createChooser(
                                intent,
                                CHOOSER_TITLE
                            ), REQUEST_GALLERY
                        )
                    }
                }
            }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CAMERA -> cameraCallback?.invoke(capturedImageURI)
                REQUEST_GALLERY -> galleryCallback?.invoke(data?.data)
            }
        }
    }

    private fun getContextFromHost(): FragmentActivity {
        return when (host) {
            is PermissionsHost.ActivitySeal -> host.activity as FragmentActivity
            is PermissionsHost.FragmentSeal -> host.frag.activity!!
        }
    }

    private sealed class PermissionsHost {
        class ActivitySeal(val activity: Activity) : PermissionsHost()
        class FragmentSeal(val frag: Fragment) : PermissionsHost()
    }

}

fun getRealPathFromURI(context: Context, contentUri: Uri): String {
    var cursor: Cursor? = null
    return try {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        cursor = context.contentResolver.query(contentUri, proj, null, null, null)
        cursor?.moveToFirst()
        val columnIndex = cursor!!.getColumnIndexOrThrow(proj[0])
        cursor.getString(columnIndex)
    } catch (e: Exception) {
        Log.e("PATHURI", "getRealPathFromURI Exception : $e")
        ""
    } finally {
        cursor?.close()
    }
}
