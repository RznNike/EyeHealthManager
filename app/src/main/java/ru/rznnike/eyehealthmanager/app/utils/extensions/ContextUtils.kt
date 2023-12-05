package ru.rznnike.eyehealthmanager.app.utils.extensions

import android.app.DownloadManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Rect
import android.net.Uri
import android.os.*
import android.util.DisplayMetrics
import android.util.TypedValue
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.window.layout.WindowMetricsCalculator
import ru.rznnike.eyehealthmanager.BuildConfig
import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.app.dispatcher.notifier.Notifier
import ru.rznnike.eyehealthmanager.domain.model.RemoteFile
import java.io.File
import java.util.*

fun Context.convertDpToPx(value: Float) = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    value,
    resources.displayMetrics
)

fun Context.convertPxToDp(value: Float) =
    value * (resources.displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)

fun Context.convertMmToPx(value: Float) = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_MM,
    value,
    resources.displayMetrics
)

fun Context.convertPxToMm(value: Float) =
    value / (resources.displayMetrics.xdpi * (1.0f / 25.4f))


fun Context.convertSpToPx(value: Float) = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_SP,
    value,
    resources.displayMetrics
)

val Fragment.deviceSize: Rect
    get() {
        return WindowMetricsCalculator.getOrCreate()
            .computeCurrentWindowMetrics(requireActivity())
            .bounds
    }

val Context.isNightModeEnabled: Boolean
    get() {
        val uiModeFlag = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return uiModeFlag == Configuration.UI_MODE_NIGHT_YES
    }

@Suppress("DEPRECATION")
fun Context.openFile(
    remoteFile: RemoteFile,
    notifier: Notifier
) {
    if (remoteFile.filePath.startsWith("http")) {
        val request = DownloadManager.Request(Uri.parse(remoteFile.filePath))
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            .setAllowedOverRoaming(false)
            .setVisibleInDownloadsUi(true)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                "%s/%s".format(
                    getString(R.string.app_name),
                    remoteFile.fileName?.replace("[\\\\|/:*?\"<>]".toRegex(), "_")
                )
            )
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            request.allowScanningByMediaScanner()
        }
        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)
        notifier.sendMessage(getString(R.string.download_start_notification))
    } else {
        val mimeType = MimeTypeMap.getSingleton()
            .getMimeTypeFromExtension(remoteFile.filePath.substringAfterLast("."))
        val newIntent = Intent(Intent.ACTION_VIEW)
        val uri = FileProvider.getUriForFile(
            this,
            BuildConfig.APPLICATION_ID + ".file_provider",
            File(remoteFile.filePath)
        )
        newIntent.setDataAndType(uri, mimeType)
        newIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
        try {
            startActivity(newIntent)
        } catch (e: ActivityNotFoundException) {
            Intent.createChooser(newIntent, remoteFile.fileName ?: "")
        }
    }
}
