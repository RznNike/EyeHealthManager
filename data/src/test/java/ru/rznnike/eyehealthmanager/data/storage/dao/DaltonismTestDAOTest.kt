package ru.rznnike.eyehealthmanager.data.storage.dao

import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import ru.rznnike.eyehealthmanager.data.storage.AbstractObjectBoxTest
import ru.rznnike.eyehealthmanager.data.storage.entity.DaltonismTestEntity
import ru.rznnike.eyehealthmanager.domain.model.test.daltonism.DaltonismAnomalyType

class DaltonismTestDAOTest : AbstractObjectBoxTest() {
    @Test
    fun add_newEntity_success() {
        val dao = DaltonismTestDAO(store!!)
        val entity = DaltonismTestEntity(
            id = 0,
            errorsCount = 1,
            anomalyType = DaltonismAnomalyType.NONE
        )

        val id = dao.add(entity)
        val newEntity = dao.get(id)

        assertNotNull(newEntity)
        assertEquals(entity.id, newEntity?.id)
        assertEquals(entity.errorsCount, newEntity?.errorsCount)
        assertEquals(entity.anomalyType, newEntity?.anomalyType)
    }

    @Test
    fun add_existingEntity_overwrite() {
        val dao = DaltonismTestDAO(store!!)
        val entity1 = DaltonismTestEntity(
            id = 0,
            errorsCount = 1,
            anomalyType = DaltonismAnomalyType.NONE
        )
        val entity2 = DaltonismTestEntity(
            id = 0,
            errorsCount = 2,
            anomalyType = DaltonismAnomalyType.PATHOLOGY
        )

        val id = dao.add(entity1)
        dao.add(entity2.copy(id = id))
        val newEntity = dao.get(id)

        assertNotNull(newEntity)
        assertEquals(id, newEntity?.id)
        assertEquals(entity2.errorsCount, newEntity?.errorsCount)
        assertEquals(entity2.anomalyType, newEntity?.anomalyType)
    }

    @Test
    fun get_badId_null() {
        val dao = DaltonismTestDAO(store!!)
        val entity = DaltonismTestEntity(
            id = 0,
            errorsCount = 1,
            anomalyType = DaltonismAnomalyType.NONE
        )

        val id = dao.add(entity)
        val newEntity = dao.get(id + 10)

        assertNull(newEntity)
    }

    @Test
    fun delete_correctId_success() {
        val dao = DaltonismTestDAO(store!!)
        val entity = DaltonismTestEntity(
            id = 0,
            errorsCount = 1,
            anomalyType = DaltonismAnomalyType.NONE
        )

        val id = dao.add(entity)
        val result = dao.delete(id)
        val newEntity = dao.get(id)

        assertTrue(result)
        assertNull(newEntity)
    }

    @Test
    fun delete_badId_noError() {
        val dao = DaltonismTestDAO(store!!)
        val entity = DaltonismTestEntity(
            id = 0,
            errorsCount = 1,
            anomalyType = DaltonismAnomalyType.NONE
        )

        val id = dao.add(entity)
        val result = dao.delete(id + 10)

        assertFalse(result)
    }

    @Test
    fun deleteAll_noData_success() {
        val dao = DaltonismTestDAO(store!!)

        dao.deleteAll()

        assertDoesNotThrow {
            dao.deleteAll()
        }
    }

    @Test
    fun deleteAll_withData_success() {
        val dao = DaltonismTestDAO(store!!)
        val entity1 = DaltonismTestEntity(
            id = 0,
            errorsCount = 1,
            anomalyType = DaltonismAnomalyType.NONE
        )
        val entity2 = DaltonismTestEntity(
            id = 0,
            errorsCount = 2,
            anomalyType = DaltonismAnomalyType.PATHOLOGY
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