package ru.rznnike.eyehealthmanager.data.storage.dao

import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import ru.rznnike.eyehealthmanager.data.storage.AbstractObjectBoxTest
import ru.rznnike.eyehealthmanager.data.storage.entity.NearFarTestEntity
import ru.rznnike.eyehealthmanager.domain.model.test.nearfar.NearFarAnswerType

class NearFarTestDAOTest : AbstractObjectBoxTest() {
    @Test
    fun add_newEntity_success() {
        val dao = NearFarTestDAO(store!!)
        val entity = NearFarTestEntity(
            id = 0,
            resultLeftEye = NearFarAnswerType.EQUAL,
            resultRightEye = NearFarAnswerType.GREEN_BETTER
        )

        val id = dao.add(entity)
        val newEntity = dao.get(id)

        assertNotNull(newEntity)
        assertEquals(entity.id, newEntity?.id)
        assertEquals(entity.resultLeftEye, newEntity?.resultLeftEye)
        assertEquals(entity.resultRightEye, newEntity?.resultRightEye)
    }

    @Test
    fun add_existingEntity_overwrite() {
        val dao = NearFarTestDAO(store!!)
        val entity1 = NearFarTestEntity(
            id = 0,
            resultLeftEye = NearFarAnswerType.EQUAL,
            resultRightEye = NearFarAnswerType.GREEN_BETTER
        )
        val entity2 = NearFarTestEntity(
            id = 0,
            resultLeftEye = NearFarAnswerType.EQUAL,
            resultRightEye = NearFarAnswerType.RED_BETTER
        )

        val id = dao.add(entity1)
        dao.add(entity2.copy(id = id))
        val newEntity = dao.get(id)

        assertNotNull(newEntity)
        assertEquals(id, newEntity?.id)
        assertEquals(entity2.resultLeftEye, newEntity?.resultLeftEye)
        assertEquals(entity2.resultRightEye, newEntity?.resultRightEye)
    }

    @Test
    fun get_badId_null() {
        val dao = NearFarTestDAO(store!!)
        val entity = NearFarTestEntity(
            id = 0,
            resultLeftEye = NearFarAnswerType.EQUAL,
            resultRightEye = NearFarAnswerType.GREEN_BETTER
        )

        val id = dao.add(entity)
        val newEntity = dao.get(id + 10)

        assertNull(newEntity)
    }

    @Test
    fun delete_correctId_success() {
        val dao = NearFarTestDAO(store!!)
        val entity = NearFarTestEntity(
            id = 0,
            resultLeftEye = NearFarAnswerType.EQUAL,
            resultRightEye = NearFarAnswerType.GREEN_BETTER
        )

        val id = dao.add(entity)
        val result = dao.delete(id)
        val newEntity = dao.get(id)

        assertTrue(result)
        assertNull(newEntity)
    }

    @Test
    fun delete_badId_noError() {
        val dao = NearFarTestDAO(store!!)
        val entity = NearFarTestEntity(
            id = 0,
            resultLeftEye = NearFarAnswerType.EQUAL,
            resultRightEye = NearFarAnswerType.GREEN_BETTER
        )

        val id = dao.add(entity)
        val result = dao.delete(id + 10)

        assertFalse(result)
    }

    @Test
    fun deleteAll_noData_success() {
        val dao = NearFarTestDAO(store!!)

        dao.deleteAll()

        assertDoesNotThrow {
            dao.deleteAll()
        }
    }

    @Test
    fun deleteAll_withData_success() {
        val dao = NearFarTestDAO(store!!)
        val entity1 = NearFarTestEntity(
            id = 0,
            resultLeftEye = NearFarAnswerType.EQUAL,
            resultRightEye = NearFarAnswerType.GREEN_BETTER
        )
        val entity2 = NearFarTestEntity(
            id = 0,
            resultLeftEye = NearFarAnswerType.EQUAL,
            resultRightEye = NearFarAnswerType.RED_BETTER
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