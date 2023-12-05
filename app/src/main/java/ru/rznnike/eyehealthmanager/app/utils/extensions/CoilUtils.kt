package ru.rznnike.eyehealthmanager.app.utils.extensions

import android.view.Gravity
import android.widget.ImageView
import androidx.annotation.DrawableRes
import coil.ImageLoader
import coil.imageLoader
import coil.load
import coil.request.Disposable
import coil.request.ImageRequest
import ru.rznnike.eyehealthmanager.domain.model.RemoteFile
import java.io.File

fun ImageRequest.Builder.default(
    imageView: ImageView,
    @DrawableRes drawableResId: Int
) {
    imageView.foregroundGravity = Gravity.CENTER
    imageView.setForegroundRes(drawableResId)
    listener(
        onSuccess = { _, _ ->
            imageView.foreground = null
        }
    )
}

fun ImageView.load(
    remoteFile: RemoteFile,
    imageLoader: ImageLoader = context.imageLoader,
    builderTransformation: ImageRequest.Builder.() -> Unit = {}
): Disposable = when {
    remoteFile.uri != null -> load(
        data = remoteFile.uri,
        imageLoader = imageLoader,
        builder = builderTransformation
    )
    remoteFile.filePath.startsWith("http") -> load(
        data = remoteFile.filePath,
        imageLoader = imageLoader,
        builder = {
            builderTransformation()
        }
    )
    File(remoteFile.filePath).exists() -> load(
        data = File(remoteFile.filePath),
        imageLoader = imageLoader,
        builder = builderTransformation
    )
    else -> load(
        data = remoteFile.filePath,
        imageLoader = imageLoader,
        builder = builderTransformation
    )
}
