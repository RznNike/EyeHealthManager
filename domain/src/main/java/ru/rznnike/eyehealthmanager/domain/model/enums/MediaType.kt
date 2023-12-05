package ru.rznnike.eyehealthmanager.domain.model.enums

import android.os.Parcelable
import android.webkit.MimeTypeMap
import kotlinx.parcelize.Parcelize

@Parcelize
enum class MediaType(
    val id: Int,
    val mask: Regex?
): Parcelable {
    IMAGE(0, Regex("image/.+")),
    FILE(1, null);

    companion object {
        operator fun get(id: Int?) = values().find { it.id == id } ?: FILE

        fun getByMimeType(mimeType: String?): MediaType =
            values().firstOrNull {
                it.mask?.let { mask ->
                    mimeType?.matches(mask)
                } ?: false
            } ?: FILE

        fun getByFileNameOrPath(fileName: String?): MediaType {
            val mimeType = MimeTypeMap.getSingleton()
                .getMimeTypeFromExtension(
                    (fileName?.substringAfterLast(".") ?: "").lowercase()
                )
            return getByMimeType(mimeType)
        }
    }
}