package ru.rznnike.eyehealthmanager.data.storage

import io.objectbox.BoxStore
import io.objectbox.config.DebugFlags
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import ru.rznnike.eyehealthmanager.data.storage.entity.MyObjectBox
import java.io.File

open class AbstractObjectBoxTest {
    protected var store: BoxStore? = null
        private set

    @BeforeEach
    fun beforeEach() {
        BoxStore.deleteAllFiles(TEST_DIRECTORY)
        store = MyObjectBox.builder()
            .directory(TEST_DIRECTORY)
            .debugFlags(DebugFlags.LOG_QUERIES or DebugFlags.LOG_QUERY_PARAMETERS)
            .build()
    }

    @AfterEach
    fun afterEach() {
        store?.close()
        BoxStore.deleteAllFiles(TEST_DIRECTORY)
    }

    companion object {
        private val TEST_DIRECTORY = File("unit_tests/test_db")
    }
}