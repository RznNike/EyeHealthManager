package ru.rznnike.eyehealthmanager.app.utils.extensions

fun <T> List<T>.smartFilter(
    query: String,
    stringRetrievers: List<(T) -> String>
): List<T> {
    val preparedQuery = query.prepareTextForSearch()

    return filter { item ->
        stringRetrievers.any { retriever ->
            retriever(item)
                .prepareTextForSearch()
                .contains(preparedQuery)
        }
    }
}

fun String.prepareTextForSearch() = trim()
    .lowercase()
    .replace("ё", "е")