package ru.rznnike.eyehealthmanager.data.storage.dao

import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import ru.rznnike.eyehealthmanager.data.storage.AbstractObjectBoxTest
import ru.rznnike.eyehealthmanager.data.storage.entity.ColorPerceptionTestEntity

class ColorPerceptionTestDAOTest : AbstractObjectBoxTest() {
    @Test
    fun add_newEntity_success() {
        val dao = ColorPerceptionTestDAO(store!!)
        val entity = ColorPerceptionTestEntity(
            id = 0,
            recognizedColorsCount = 10,
            allColorsCount = 20
        )

        val id = dao.add(entity)
        val newEntity = dao.get(id)

        assertNotNull(newEntity)
        assertEquals(entity.id, newEntity?.id)
        assertEquals(entity.recognizedColorsCount, newEntity?.recognizedColorsCount)
        assertEquals(entity.recognizedColorsCount, newEntity?.recognizedColorsCount)
    }

    @Test
    fun add_existingEntity_overwrite() {
        val dao = ColorPerceptionTestDAO(store!!)
        val entity1 = ColorPerceptionTestEntity(
            id = 0,
            recognizedColorsCount = 10,
            allColorsCount = 20
        )
        val entity2 = ColorPerceptionTestEntity(
            id = 0,
            recognizedColorsCount = 15,
            allColorsCount = 20
        )

        val id = dao.add(entity1)
        dao.add(entity2.copy(id = id))
        val newEntity = dao.get(id)

        assertNotNull(newEntity)
        assertEquals(id, newEntity?.id)
        assertEquals(entity2.recognizedColorsCount, newEntity?.recognizedColorsCount)
        assertEquals(entity2.recognizedColorsCount, newEntity?.recognizedColorsCount)
    }

    @Test
    fun get_badId_null() {
        val dao = ColorPerceptionTestDAO(store!!)
        val entity = ColorPerceptionTestEntity(
            id = 0,
            recognizedColorsCount = 10,
            allColorsCount = 20
        )

        val id = dao.add(entity)
        val newEntity = dao.get(id + 10)

        assertNull(newEntity)
    }

    @Test
    fun delete_correctId_success() {
        val dao = ColorPerceptionTestDAO(store!!)
        val entity = ColorPerceptionTestEntity(
            id = 0,
            recognizedColorsCount = 10,
            allColorsCount = 20
        )

        val id = dao.add(entity)
        val result = dao.delete(id)
        val newEntity = dao.get(id)

        assertTrue(result)
        assertNull(newEntity)
    }

    @Test
    fun delete_badId_noError() {
        val dao = ColorPerceptionTestDAO(store!!)
        val entity = ColorPerceptionTestEntity(
            id = 0,
            recognizedColorsCount = 10,
            allColorsCount = 20
        )

        val id = dao.add(entity)
        val result = dao.delete(id + 10)

        assertFalse(result)
    }

    @Test
    fun deleteAll_noData_success() {
        val dao = ColorPerceptionTestDAO(store!!)

        dao.deleteAll()

        assertDoesNotThrow {
            dao.deleteAll()
        }
    }

    @Test
    fun deleteAll_withData_success() {
        val dao = ColorPerceptionTestDAO(store!!)
        val entity1 = ColorPerceptionTestEntity(
            id = 0,
            recognizedColorsCount = 10,
            allColorsCount = 20
        )
        val entity2 = ColorPerceptionTestEntity(
            id = 0,
            recognizedColorsCount = 15,
            allColorsCount = 20
        )

        assertDoesNotThrow {
            val id1 = dao.add(entity1)
            val id2 = dao.add(entity2)
            dao.deleteAll()
            val result = listOfNotNull(
                dao.get(id1),
                dao.get(id2)
            )

            assertTrue(result.isEmpty())
        }
    }
}