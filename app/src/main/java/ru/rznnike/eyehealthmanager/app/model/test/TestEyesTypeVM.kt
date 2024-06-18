package ru.rznnike.eyehealthmanager.app.model.test

import androidx.annotation.StringRes
import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.domain.model.test.TestEyesType

enum class TestEyesTypeVM(
    val data: TestEyesType,
    @StringRes val nameResId: Int
) {
    BOTH(
        data = TestEyesType.BOTH,
        nameResId = R.string.eyes_type_both
    ),
    LEFT(
        data = TestEyesType.LEFT,
        nameResId = R.string.eyes_type_left
    ),
    RIGHT(
        data = TestEyesType.RIGHT,
        nameResId = R.string.eyes_type_right
    );

    companion object {
        operator fun get(data: TestEyesType?) = entries.find { it.data == data } ?: BOTH
    }
}