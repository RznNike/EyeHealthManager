package ru.rznnike.eyehealthmanager.data.storage.dao

import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import ru.rznnike.eyehealthmanager.data.storage.AbstractObjectBoxTest
import ru.rznnike.eyehealthmanager.data.storage.entity.AcuityTestEntity
import ru.rznnike.eyehealthmanager.domain.model.enums.AcuityTestSymbolsType
import ru.rznnike.eyehealthmanager.domain.model.enums.DayPart
import ru.rznnike.eyehealthmanager.domain.model.enums.TestEyesType

class AcuityTestDAOTest : AbstractObjectBoxTest() {
    @Test
    fun add_newEntity_success() {
        val dao = AcuityTestDAO(store!!)
        val entity = AcuityTestEntity(
            id = 0,
            symbolsType = AcuityTestSymbolsType.LETTERS_RU,
            testEyesType = TestEyesType.BOTH,
            dayPart = DayPart.MIDDLE,
            resultLeftEye = 0,
            resultRightEye = 10,
            measuredByDoctor = false
        )

        val id = dao.add(entity)
        val newEntity = dao.get(id)

        assertNotNull(newEntity)
        assertEquals(entity.id, newEntity?.id)
        assertEquals(entity.symbolsType, newEntity?.symbolsType)
        assertEquals(entity.testEyesType, newEntity?.testEyesType)
        assertEquals(entity.dayPart, newEntity?.dayPart)
        assertEquals(entity.resultLeftEye, newEntity?.resultLeftEye)
        assertEquals(entity.resultRightEye, newEntity?.resultRightEye)
        assertEquals(entity.measuredByDoctor, newEntity?.measuredByDoctor)
    }

    @Test
    fun add_existingEntity_overwrite() {
        val dao = AcuityTestDAO(store!!)
        val entity1 = AcuityTestEntity(
            id = 0,
            symbolsType = AcuityTestSymbolsType.LETTERS_RU,
            testEyesType = TestEyesType.BOTH,
            dayPart = DayPart.MIDDLE,
            resultLeftEye = 0,
            resultRightEye = 10,
            measuredByDoctor = false
        )
        val entity2 = AcuityTestEntity(
            id = 0,
            symbolsType = AcuityTestSymbolsType.LETTERS_EN,
            testEyesType = TestEyesType.LEFT,
            dayPart = DayPart.BEGINNING,
            resultLeftEye = 10,
            resultRightEye = 20,
            measuredByDoctor = true
        )

        val id = dao.add(entity1)
        dao.add(entity2.copy(id = id))
        val newEntity = dao.get(id)

        assertNotNull(newEntity)
        assertEquals(id, newEntity?.id)
        assertEquals(entity2.symbolsType, newEntity?.symbolsType)
        assertEquals(entity2.testEyesType, newEntity?.testEyesType)
        assertEquals(entity2.dayPart, newEntity?.dayPart)
        assertEquals(entity2.resultLeftEye, newEntity?.resultLeftEye)
        assertEquals(entity2.resultRightEye, newEntity?.resultRightEye)
        assertEquals(entity2.measuredByDoctor, newEntity?.measuredByDoctor)
    }

    @Test
    fun get_badId_null() {
        val dao = AcuityTestDAO(store!!)
        val entity = AcuityTestEntity(
            id = 0,
            symbolsType = AcuityTestSymbolsType.LETTERS_RU,
            testEyesType = TestEyesType.BOTH,
            dayPart = DayPart.MIDDLE,
            resultLeftEye = 0,
            resultRightEye = 10,
            measuredByDoctor = false
        )

        val id = dao.add(entity)
        val newEntity = dao.get(id + 10)

        assertNull(newEntity)
    }

    @Test
    fun delete_correctId_success() {
        val dao = AcuityTestDAO(store!!)
        val entity = AcuityTestEntity(
            id = 0,
            symbolsType = AcuityTestSymbolsType.LETTERS_RU,
            testEyesType = TestEyesType.BOTH,
            dayPart = DayPart.MIDDLE,
            resultLeftEye = 0,
            resultRightEye = 10,
            measuredByDoctor = false
        )

        val id = dao.add(entity)
        val result = dao.delete(id)
        val newEntity = dao.get(id)

        assertTrue(result)
        assertNull(newEntity)
    }

    @Test
    fun delete_badId_noError() {
        val dao = AcuityTestDAO(store!!)
        val entity = AcuityTestEntity(
            id = 0,
            symbolsType = AcuityTestSymbolsType.LETTERS_RU,
            testEyesType = TestEyesType.BOTH,
            dayPart = DayPart.MIDDLE,
            resultLeftEye = 0,
            resultRightEye = 10,
            measuredByDoctor = false
        )

        val id = dao.add(entity)
        val result = dao.delete(id + 10)

        assertFalse(result)
    }

    @Test
    fun deleteAll_noData_success() {
        val dao = AcuityTestDAO(store!!)

        dao.deleteAll()

        assertDoesNotThrow {
            dao.deleteAll()
        }
    }

    @Test
    fun deleteAll_withData_success() {
        val dao = AcuityTestDAO(store!!)
        val entity1 = AcuityTestEntity(
            id = 0,
            symbolsType = AcuityTestSymbolsType.LETTERS_RU,
            testEyesType = TestEyesType.BOTH,
            dayPart = DayPart.MIDDLE,
            resultLeftEye = 0,
            resultRightEye = 10,
            measuredByDoctor = false
        )
        val entity2 = AcuityTestEntity(
            id = 0,
            symbolsType = AcuityTestSymbolsType.LETTERS_EN,
            testEyesType = TestEyesType.LEFT,
            dayPart = DayPart.BEGINNING,
            resultLeftEye = 10,
            resultRightEye = 20,
            measuredByDoctor = true
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