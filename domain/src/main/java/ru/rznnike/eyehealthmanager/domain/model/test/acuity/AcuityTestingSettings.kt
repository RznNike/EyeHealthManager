package ru.rznnike.eyehealthmanager.domain.model.test.acuity

import ru.rznnike.eyehealthmanager.domain.model.test.TestEyesType

data class AcuityTestingSettings(
    var symbolsType: AcuityTestSymbolsType = AcuityTestSymbolsType.LETTERS_EN,
    var eyesType: TestEyesType = TestEyesType.BOTH
)