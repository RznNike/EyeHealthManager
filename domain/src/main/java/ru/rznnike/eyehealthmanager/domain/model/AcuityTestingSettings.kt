package ru.rznnike.eyehealthmanager.domain.model

import ru.rznnike.eyehealthmanager.domain.model.enums.AcuityTestSymbolsType
import ru.rznnike.eyehealthmanager.domain.model.enums.TestEyesType

data class AcuityTestingSettings(
    var symbolsType: AcuityTestSymbolsType = AcuityTestSymbolsType.LETTERS_EN,
    var eyesType: TestEyesType = TestEyesType.BOTH
)