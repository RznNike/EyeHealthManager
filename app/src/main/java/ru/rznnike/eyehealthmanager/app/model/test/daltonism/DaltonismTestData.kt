package ru.rznnike.eyehealthmanager.app.model.test.daltonism

import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.domain.model.test.daltonism.DaltonismAnomalyType

object DaltonismTestData {
    val questions: List<DaltonismTestQuestion> = listOf(
        DaltonismTestQuestion(
            testImageResId = R.drawable.rabkin_1,
            answerResIds = listOf(
                R.string.rabkin_96,
                R.string.rabkin_9,
                R.string.rabkin_80,
                R.string.rabkin_86
            ),
            answerVariantsMap = mapOf(
                DaltonismAnomalyType.PROTANOPIA to listOf(0),
                DaltonismAnomalyType.DEITERANOPIA to listOf(0)
            ),
            answerBooleanMap = mapOf(
                DaltonismAnomalyType.PROTANOMALY_A to true,
                DaltonismAnomalyType.PROTANOMALY_B to true,
                DaltonismAnomalyType.PROTANOMALY_C to true,
                DaltonismAnomalyType.DEITERANOMALY_A to true,
                DaltonismAnomalyType.DEITERANOMALY_B to true,
                DaltonismAnomalyType.DEITERANOMALY_C to true,
                DaltonismAnomalyType.PATHOLOGY to true
            )
        ),
        DaltonismTestQuestion(
            testImageResId = R.drawable.rabkin_2,
            answerResIds = listOf(
                R.string.rabkin_circle_and_triangle,
                R.string.rabkin_circle,
                R.string.rabkin_triangle,
                R.string.rabkin_square
            ),
            answerVariantsMap = mapOf(
                DaltonismAnomalyType.PROTANOPIA to listOf(0),
                DaltonismAnomalyType.DEITERANOPIA to listOf(0)
            ),
            answerBooleanMap = mapOf(
                DaltonismAnomalyType.PROTANOMALY_A to true,
                DaltonismAnomalyType.PROTANOMALY_B to true,
                DaltonismAnomalyType.PROTANOMALY_C to true,
                DaltonismAnomalyType.DEITERANOMALY_A to true,
                DaltonismAnomalyType.DEITERANOMALY_B to true,
                DaltonismAnomalyType.DEITERANOMALY_C to true,
                DaltonismAnomalyType.PATHOLOGY to true
            )
        ),
        DaltonismTestQuestion(
            testImageResId = R.drawable.rabkin_3,
            answerResIds = listOf(
                R.string.rabkin_9,
                R.string.rabkin_5,
                R.string.rabkin_3,
                R.string.rabkin_8
            ),
            answerVariantsMap = mapOf(
                DaltonismAnomalyType.PROTANOPIA to listOf(1),
                DaltonismAnomalyType.DEITERANOPIA to listOf(1)
            ),
            answerBooleanMap = mapOf(
                DaltonismAnomalyType.PROTANOMALY_A to false,
                DaltonismAnomalyType.PROTANOMALY_B to false,
                DaltonismAnomalyType.PROTANOMALY_C to false,
                DaltonismAnomalyType.DEITERANOMALY_A to false,
                DaltonismAnomalyType.DEITERANOMALY_B to false,
                DaltonismAnomalyType.DEITERANOMALY_C to false,
                DaltonismAnomalyType.PATHOLOGY to true
            )
        ),
        DaltonismTestQuestion(
            testImageResId = R.drawable.rabkin_4,
            answerResIds = listOf(
                R.string.rabkin_triangle,
                R.string.rabkin_circle,
                R.string.rabkin_square,
                R.string.rabkin_circle_and_triangle
            ),
            answerVariantsMap = mapOf(
                DaltonismAnomalyType.PROTANOPIA to listOf(1),
                DaltonismAnomalyType.DEITERANOPIA to listOf(1)
            ),
            answerBooleanMap = mapOf(
                DaltonismAnomalyType.PROTANOMALY_A to false,
                DaltonismAnomalyType.PROTANOMALY_B to false,
                DaltonismAnomalyType.PROTANOMALY_C to false,
                DaltonismAnomalyType.DEITERANOMALY_A to false,
                DaltonismAnomalyType.DEITERANOMALY_B to false,
                DaltonismAnomalyType.DEITERANOMALY_C to false,
                DaltonismAnomalyType.PATHOLOGY to false
            )
        ),
        DaltonismTestQuestion(
            testImageResId = R.drawable.rabkin_5,
            answerResIds = listOf(
                R.string.rabkin_13,
                R.string.rabkin_6,
                R.string.rabkin_8,
                R.string.rabkin_2
            ),
            answerVariantsMap = mapOf(
                DaltonismAnomalyType.PROTANOPIA to listOf(1),
                DaltonismAnomalyType.DEITERANOPIA to listOf(1)
            ),
            answerBooleanMap = mapOf(
                DaltonismAnomalyType.PROTANOMALY_A to false,
                DaltonismAnomalyType.PROTANOMALY_B to false,
                DaltonismAnomalyType.PROTANOMALY_C to false,
                DaltonismAnomalyType.DEITERANOMALY_A to false,
                DaltonismAnomalyType.DEITERANOMALY_B to false,
                DaltonismAnomalyType.DEITERANOMALY_C to false,
                DaltonismAnomalyType.PATHOLOGY to false
            )
        ),
        DaltonismTestQuestion(
            testImageResId = R.drawable.rabkin_6,
            answerResIds = listOf(
                R.string.rabkin_circle_and_triangle,
                R.string.rabkin_circle,
                R.string.rabkin_triangle,
                R.string.rabkin_nothing
            ),
            answerVariantsMap = mapOf(
                DaltonismAnomalyType.PROTANOPIA to listOf(3),
                DaltonismAnomalyType.DEITERANOPIA to listOf(3)
            ),
            answerBooleanMap = mapOf(
                DaltonismAnomalyType.PROTANOMALY_A to false,
                DaltonismAnomalyType.PROTANOMALY_B to false,
                DaltonismAnomalyType.PROTANOMALY_C to false,
                DaltonismAnomalyType.DEITERANOMALY_A to false,
                DaltonismAnomalyType.DEITERANOMALY_B to false,
                DaltonismAnomalyType.DEITERANOMALY_C to false,
                DaltonismAnomalyType.PATHOLOGY to false
            )
        ),
        DaltonismTestQuestion(
            testImageResId = R.drawable.rabkin_7,
            answerResIds = listOf(
                R.string.rabkin_96,
                R.string.rabkin_9,
                R.string.rabkin_6,
                R.string.rabkin_nothing
            ),
            answerVariantsMap = mapOf(
                DaltonismAnomalyType.PROTANOPIA to listOf(0),
                DaltonismAnomalyType.DEITERANOPIA to listOf(2)
            ),
            answerBooleanMap = mapOf(
                DaltonismAnomalyType.PROTANOMALY_A to true,
                DaltonismAnomalyType.PROTANOMALY_B to true,
                DaltonismAnomalyType.PROTANOMALY_C to true,
                DaltonismAnomalyType.DEITERANOMALY_A to false,
                DaltonismAnomalyType.DEITERANOMALY_B to false,
                DaltonismAnomalyType.DEITERANOMALY_C to true,
                DaltonismAnomalyType.PATHOLOGY to false
            )
        ),
        DaltonismTestQuestion(
            testImageResId = R.drawable.rabkin_8,
            answerResIds = listOf(
                R.string.rabkin_5,
                R.string.rabkin_6,
                R.string.rabkin_8,
                R.string.rabkin_9
            ),
            answerVariantsMap = mapOf(
                DaltonismAnomalyType.PROTANOPIA to listOf(0),
                DaltonismAnomalyType.DEITERANOPIA to listOf(0)
            ),
            answerBooleanMap = mapOf(
                DaltonismAnomalyType.PROTANOMALY_A to true,
                DaltonismAnomalyType.PROTANOMALY_B to true,
                DaltonismAnomalyType.PROTANOMALY_C to true,
                DaltonismAnomalyType.DEITERANOMALY_A to true,
                DaltonismAnomalyType.DEITERANOMALY_B to true,
                DaltonismAnomalyType.DEITERANOMALY_C to true,
                DaltonismAnomalyType.PATHOLOGY to false
            )
        ),
        DaltonismTestQuestion(
            testImageResId = R.drawable.rabkin_9,
            answerResIds = listOf(
                R.string.rabkin_9,
                R.string.rabkin_6,
                R.string.rabkin_8,
                R.string.rabkin_5
            ),
            answerVariantsMap = mapOf(
                DaltonismAnomalyType.PROTANOPIA to listOf(1, 2),
                DaltonismAnomalyType.DEITERANOPIA to listOf(0)
            ),
            answerBooleanMap = mapOf(
                DaltonismAnomalyType.PROTANOMALY_A to false,
                DaltonismAnomalyType.PROTANOMALY_B to false,
                DaltonismAnomalyType.PROTANOMALY_C to false,
                DaltonismAnomalyType.DEITERANOMALY_A to true,
                DaltonismAnomalyType.DEITERANOMALY_B to true,
                DaltonismAnomalyType.DEITERANOMALY_C to true,
                DaltonismAnomalyType.PATHOLOGY to true
            )
        ),
        DaltonismTestQuestion(
            testImageResId = R.drawable.rabkin_10,
            answerResIds = listOf(
                R.string.rabkin_136,
                R.string.rabkin_66,
                R.string.rabkin_68,
                R.string.rabkin_69
            ),
            answerVariantsMap = mapOf(
                DaltonismAnomalyType.PROTANOPIA to listOf(2, 3),
                DaltonismAnomalyType.DEITERANOPIA to listOf(1, 3)
            ),
            answerBooleanMap = mapOf(
                DaltonismAnomalyType.PROTANOMALY_A to false,
                DaltonismAnomalyType.PROTANOMALY_B to false,
                DaltonismAnomalyType.PROTANOMALY_C to false,
                DaltonismAnomalyType.DEITERANOMALY_A to false,
                DaltonismAnomalyType.DEITERANOMALY_B to false,
                DaltonismAnomalyType.DEITERANOMALY_C to false,
                DaltonismAnomalyType.PATHOLOGY to false
            )
        ),
        DaltonismTestQuestion(
            testImageResId = R.drawable.rabkin_11,
            answerResIds = listOf(
                R.string.rabkin_circle_and_triangle,
                R.string.rabkin_circle,
                R.string.rabkin_triangle,
                R.string.rabkin_square
            ),
            answerVariantsMap = mapOf(
                DaltonismAnomalyType.PROTANOPIA to listOf(2),
                DaltonismAnomalyType.DEITERANOPIA to listOf(0, 1)
            ),
            answerBooleanMap = mapOf(
                DaltonismAnomalyType.PROTANOMALY_A to false,
                DaltonismAnomalyType.PROTANOMALY_B to false,
                DaltonismAnomalyType.PROTANOMALY_C to false,
                DaltonismAnomalyType.DEITERANOMALY_A to true,
                DaltonismAnomalyType.DEITERANOMALY_B to true,
                DaltonismAnomalyType.DEITERANOMALY_C to true,
                DaltonismAnomalyType.PATHOLOGY to true
            )
        ),
        DaltonismTestQuestion(
            testImageResId = R.drawable.rabkin_12,
            answerResIds = listOf(
                R.string.rabkin_12,
                R.string.rabkin_13,
                R.string.rabkin_8,
                R.string.rabkin_nothing
            ),
            answerVariantsMap = mapOf(
                DaltonismAnomalyType.PROTANOPIA to listOf(3),
                DaltonismAnomalyType.DEITERANOPIA to listOf(0)
            ),
            answerBooleanMap = mapOf(
                DaltonismAnomalyType.PROTANOMALY_A to false,
                DaltonismAnomalyType.PROTANOMALY_B to false,
                DaltonismAnomalyType.PROTANOMALY_C to false,
                DaltonismAnomalyType.DEITERANOMALY_A to true,
                DaltonismAnomalyType.DEITERANOMALY_B to true,
                DaltonismAnomalyType.DEITERANOMALY_C to true,
                DaltonismAnomalyType.PATHOLOGY to false
            )
        ),
        DaltonismTestQuestion(
            testImageResId = R.drawable.rabkin_13,
            answerResIds = listOf(
                R.string.rabkin_circle_and_triangle,
                R.string.rabkin_circle,
                R.string.rabkin_triangle,
                R.string.rabkin_square
            ),
            answerVariantsMap = mapOf(
                DaltonismAnomalyType.PROTANOPIA to listOf(1),
                DaltonismAnomalyType.DEITERANOPIA to listOf(2)
            ),
            answerBooleanMap = mapOf(
                DaltonismAnomalyType.PROTANOMALY_A to false,
                DaltonismAnomalyType.PROTANOMALY_B to false,
                DaltonismAnomalyType.PROTANOMALY_C to false,
                DaltonismAnomalyType.DEITERANOMALY_A to false,
                DaltonismAnomalyType.DEITERANOMALY_B to true,
                DaltonismAnomalyType.DEITERANOMALY_C to true,
                DaltonismAnomalyType.PATHOLOGY to true
            )
        ),
        DaltonismTestQuestion(
            testImageResId = R.drawable.rabkin_14,
            answerResIds = listOf(
                R.string.rabkin_30,
                R.string.rabkin_106,
                R.string.rabkin_16,
                R.string.rabkin_18
            ),
            answerVariantsMap = mapOf(
                DaltonismAnomalyType.PROTANOPIA to listOf(1),
                DaltonismAnomalyType.DEITERANOPIA to listOf(2)
            ),
            answerBooleanMap = mapOf(
                DaltonismAnomalyType.PROTANOMALY_A to false,
                DaltonismAnomalyType.PROTANOMALY_B to false,
                DaltonismAnomalyType.PROTANOMALY_C to false,
                DaltonismAnomalyType.DEITERANOMALY_A to false,
                DaltonismAnomalyType.DEITERANOMALY_B to false,
                DaltonismAnomalyType.DEITERANOMALY_C to false,
                DaltonismAnomalyType.PATHOLOGY to false
            )
        ),
        DaltonismTestQuestion(
            testImageResId = R.drawable.rabkin_15,
            answerResIds = listOf(
                R.string.rabkin_circle_and_triangle,
                R.string.rabkin_triangle,
                R.string.rabkin_triangle_and_square,
                R.string.rabkin_square
            ),
            answerVariantsMap = mapOf(
                DaltonismAnomalyType.PROTANOPIA to listOf(1, 2),
                DaltonismAnomalyType.DEITERANOPIA to listOf(2)
            ),
            answerBooleanMap = mapOf(
                DaltonismAnomalyType.PROTANOMALY_A to false,
                DaltonismAnomalyType.PROTANOMALY_B to false,
                DaltonismAnomalyType.PROTANOMALY_C to false,
                DaltonismAnomalyType.DEITERANOMALY_A to false,
                DaltonismAnomalyType.DEITERANOMALY_B to false,
                DaltonismAnomalyType.DEITERANOMALY_C to false,
                DaltonismAnomalyType.PATHOLOGY to false
            )
        ),
        DaltonismTestQuestion(
            testImageResId = R.drawable.rabkin_16,
            answerResIds = listOf(
                R.string.rabkin_96,
                R.string.rabkin_9,
                R.string.rabkin_6,
                R.string.rabkin_nothing
            ),
            answerVariantsMap = mapOf(
                DaltonismAnomalyType.PROTANOPIA to listOf(1),
                DaltonismAnomalyType.DEITERANOPIA to listOf(2)
            ),
            answerBooleanMap = mapOf(
                DaltonismAnomalyType.PROTANOMALY_A to false,
                DaltonismAnomalyType.PROTANOMALY_B to false,
                DaltonismAnomalyType.PROTANOMALY_C to true,
                DaltonismAnomalyType.DEITERANOMALY_A to false,
                DaltonismAnomalyType.DEITERANOMALY_B to false,
                DaltonismAnomalyType.DEITERANOMALY_C to true,
                DaltonismAnomalyType.PATHOLOGY to false
            )
        ),
        DaltonismTestQuestion(
            testImageResId = R.drawable.rabkin_17,
            answerResIds = listOf(
                R.string.rabkin_circle_and_triangle,
                R.string.rabkin_circle,
                R.string.rabkin_triangle,
                R.string.rabkin_square
            ),
            answerVariantsMap = mapOf(
                DaltonismAnomalyType.PROTANOPIA to listOf(2),
                DaltonismAnomalyType.DEITERANOPIA to listOf(1)
            ),
            answerBooleanMap = mapOf(
                DaltonismAnomalyType.PROTANOMALY_A to false,
                DaltonismAnomalyType.PROTANOMALY_B to false,
                DaltonismAnomalyType.PROTANOMALY_C to true,
                DaltonismAnomalyType.DEITERANOMALY_A to false,
                DaltonismAnomalyType.DEITERANOMALY_B to false,
                DaltonismAnomalyType.DEITERANOMALY_C to true,
                DaltonismAnomalyType.PATHOLOGY to false
            )
        ),
        DaltonismTestQuestion(
            testImageResId = R.drawable.rabkin_18,
            answerResIds = listOf(
                R.string.rabkin_horizontal_lines,
                R.string.rabkin_vertical_lines,
                R.string.rabkin_diagonal_lines,
                R.string.rabkin_no_color_lines
            ),
            answerVariantsMap = mapOf(
                DaltonismAnomalyType.PROTANOPIA to listOf(1),
                DaltonismAnomalyType.DEITERANOPIA to listOf(1)
            ),
            answerBooleanMap = mapOf(
                DaltonismAnomalyType.PROTANOMALY_A to false,
                DaltonismAnomalyType.PROTANOMALY_B to true,
                DaltonismAnomalyType.PROTANOMALY_C to true,
                DaltonismAnomalyType.DEITERANOMALY_A to false,
                DaltonismAnomalyType.DEITERANOMALY_B to true,
                DaltonismAnomalyType.DEITERANOMALY_C to true,
                DaltonismAnomalyType.PATHOLOGY to true
            )
        ),
        DaltonismTestQuestion(
            testImageResId = R.drawable.rabkin_19,
            answerResIds = listOf(
                R.string.rabkin_95,
                R.string.rabkin_5,
                R.string.rabkin_96,
                R.string.rabkin_6
            ),
            answerVariantsMap = mapOf(
                DaltonismAnomalyType.PROTANOPIA to listOf(1),
                DaltonismAnomalyType.DEITERANOPIA to listOf(1)
            ),
            answerBooleanMap = mapOf(
                DaltonismAnomalyType.PROTANOMALY_A to true,
                DaltonismAnomalyType.PROTANOMALY_B to true,
                DaltonismAnomalyType.PROTANOMALY_C to true,
                DaltonismAnomalyType.DEITERANOMALY_A to false,
                DaltonismAnomalyType.DEITERANOMALY_B to false,
                DaltonismAnomalyType.DEITERANOMALY_C to true,
                DaltonismAnomalyType.PATHOLOGY to true
            )
        ),
        DaltonismTestQuestion(
            testImageResId = R.drawable.rabkin_20,
            answerResIds = listOf(
                R.string.rabkin_circle_and_triangle,
                R.string.rabkin_circle,
                R.string.rabkin_triangle_and_square,
                R.string.rabkin_nothing
            ),
            answerVariantsMap = mapOf(
                DaltonismAnomalyType.PROTANOPIA to listOf(3),
                DaltonismAnomalyType.DEITERANOPIA to listOf(3)
            ),
            answerBooleanMap = mapOf(
                DaltonismAnomalyType.PROTANOMALY_A to false,
                DaltonismAnomalyType.PROTANOMALY_B to false,
                DaltonismAnomalyType.PROTANOMALY_C to false,
                DaltonismAnomalyType.DEITERANOMALY_A to false,
                DaltonismAnomalyType.DEITERANOMALY_B to false,
                DaltonismAnomalyType.DEITERANOMALY_C to false,
                DaltonismAnomalyType.PATHOLOGY to false
            )
        ),
        DaltonismTestQuestion(
            testImageResId = R.drawable.rabkin_21,
            answerResIds = listOf(
                R.string.rabkin_vertical_lines,
                R.string.rabkin_horizontal_lines,
                R.string.rabkin_diagonal_lines,
                R.string.rabkin_no_color_lines
            ),
            answerVariantsMap = mapOf(
                DaltonismAnomalyType.PROTANOPIA to listOf(1),
                DaltonismAnomalyType.DEITERANOPIA to listOf(1)
            ),
            answerBooleanMap = mapOf(
                DaltonismAnomalyType.PROTANOMALY_A to false,
                DaltonismAnomalyType.PROTANOMALY_B to true,
                DaltonismAnomalyType.PROTANOMALY_C to true,
                DaltonismAnomalyType.DEITERANOMALY_A to false,
                DaltonismAnomalyType.DEITERANOMALY_B to true,
                DaltonismAnomalyType.DEITERANOMALY_C to true,
                DaltonismAnomalyType.PATHOLOGY to false
            )
        ),
        DaltonismTestQuestion(
            testImageResId = R.drawable.rabkin_22,
            answerResIds = listOf(
                R.string.rabkin_66,
                R.string.rabkin_6,
                R.string.rabkin_96,
                R.string.rabkin_69
            ),
            answerVariantsMap = mapOf(
                DaltonismAnomalyType.PROTANOPIA to listOf(1),
                DaltonismAnomalyType.DEITERANOPIA to listOf(1)
            ),
            answerBooleanMap = mapOf(
                DaltonismAnomalyType.PROTANOMALY_A to false,
                DaltonismAnomalyType.PROTANOMALY_B to false,
                DaltonismAnomalyType.PROTANOMALY_C to false,
                DaltonismAnomalyType.DEITERANOMALY_A to false,
                DaltonismAnomalyType.DEITERANOMALY_B to false,
                DaltonismAnomalyType.DEITERANOMALY_C to true,
                DaltonismAnomalyType.PATHOLOGY to false
            )
        ),
        DaltonismTestQuestion(
            testImageResId = R.drawable.rabkin_23,
            answerResIds = listOf(
                R.string.rabkin_36,
                R.string.rabkin_66,
                R.string.rabkin_96,
                R.string.rabkin_69
            ),
            answerVariantsMap = mapOf(
                DaltonismAnomalyType.PROTANOPIA to listOf(0),
                DaltonismAnomalyType.DEITERANOPIA to listOf(0)
            ),
            answerBooleanMap = mapOf(
                DaltonismAnomalyType.PROTANOMALY_A to true,
                DaltonismAnomalyType.PROTANOMALY_B to true,
                DaltonismAnomalyType.PROTANOMALY_C to true,
                DaltonismAnomalyType.DEITERANOMALY_A to true,
                DaltonismAnomalyType.DEITERANOMALY_B to true,
                DaltonismAnomalyType.DEITERANOMALY_C to true,
                DaltonismAnomalyType.PATHOLOGY to false
            )
        ),
        DaltonismTestQuestion(
            testImageResId = R.drawable.rabkin_24,
            answerResIds = listOf(
                R.string.rabkin_14,
                R.string.rabkin_4,
                R.string.rabkin_16,
                R.string.rabkin_nothing
            ),
            answerVariantsMap = mapOf(
                DaltonismAnomalyType.PROTANOPIA to listOf(0),
                DaltonismAnomalyType.DEITERANOPIA to listOf(0)
            ),
            answerBooleanMap = mapOf(
                DaltonismAnomalyType.PROTANOMALY_A to true,
                DaltonismAnomalyType.PROTANOMALY_B to true,
                DaltonismAnomalyType.PROTANOMALY_C to true,
                DaltonismAnomalyType.DEITERANOMALY_A to true,
                DaltonismAnomalyType.DEITERANOMALY_B to true,
                DaltonismAnomalyType.DEITERANOMALY_C to true,
                DaltonismAnomalyType.PATHOLOGY to false
            )
        ),
        DaltonismTestQuestion(
            testImageResId = R.drawable.rabkin_25,
            answerResIds = listOf(
                R.string.rabkin_9,
                R.string.rabkin_6,
                R.string.rabkin_96,
                R.string.rabkin_69
            ),
            answerVariantsMap = mapOf(
                DaltonismAnomalyType.PROTANOPIA to listOf(0),
                DaltonismAnomalyType.DEITERANOPIA to listOf(0)
            ),
            answerBooleanMap = mapOf(
                DaltonismAnomalyType.PROTANOMALY_A to true,
                DaltonismAnomalyType.PROTANOMALY_B to true,
                DaltonismAnomalyType.PROTANOMALY_C to true,
                DaltonismAnomalyType.DEITERANOMALY_A to true,
                DaltonismAnomalyType.DEITERANOMALY_B to true,
                DaltonismAnomalyType.DEITERANOMALY_C to true,
                DaltonismAnomalyType.PATHOLOGY to false
            )
        ),
        DaltonismTestQuestion(
            testImageResId = R.drawable.rabkin_26,
            answerResIds = listOf(
                R.string.rabkin_4,
                R.string.rabkin_14,
                R.string.rabkin_16,
                R.string.rabkin_5
            ),
            answerVariantsMap = mapOf(
                DaltonismAnomalyType.PROTANOPIA to listOf(0),
                DaltonismAnomalyType.DEITERANOPIA to listOf(0)
            ),
            answerBooleanMap = mapOf(
                DaltonismAnomalyType.PROTANOMALY_A to true,
                DaltonismAnomalyType.PROTANOMALY_B to true,
                DaltonismAnomalyType.PROTANOMALY_C to true,
                DaltonismAnomalyType.DEITERANOMALY_A to true,
                DaltonismAnomalyType.DEITERANOMALY_B to true,
                DaltonismAnomalyType.DEITERANOMALY_C to true,
                DaltonismAnomalyType.PATHOLOGY to false
            )
        ),
        DaltonismTestQuestion(
            testImageResId = R.drawable.rabkin_27,
            answerResIds = listOf(
                R.string.rabkin_13,
                R.string.rabkin_136,
                R.string.rabkin_8,
                R.string.rabkin_nothing
            ),
            answerVariantsMap = mapOf(
                DaltonismAnomalyType.PROTANOPIA to listOf(3),
                DaltonismAnomalyType.DEITERANOPIA to listOf(3)
            ),
            answerBooleanMap = mapOf(
                DaltonismAnomalyType.PROTANOMALY_A to false,
                DaltonismAnomalyType.PROTANOMALY_B to false,
                DaltonismAnomalyType.PROTANOMALY_C to true,
                DaltonismAnomalyType.DEITERANOMALY_A to false,
                DaltonismAnomalyType.DEITERANOMALY_B to false,
                DaltonismAnomalyType.DEITERANOMALY_C to true,
                DaltonismAnomalyType.PATHOLOGY to false
            )
        )
    )
}
