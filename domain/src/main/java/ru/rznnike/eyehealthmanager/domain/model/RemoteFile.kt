package ru.rznnike.eyehealthmanager.domain.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.rznnike.eyehealthmanager.domain.model.enums.MediaType
import java.util.*

@Parcelize
data class RemoteFile(
    var uuid: String = UUID.randomUUID().toString(),
    var type: MediaType = MediaType.FILE,
    var filePath: String,
    var fileName: String? = null,
    /** Used only for picked local files */
    var uri: Uri? = null
) : Parcelable